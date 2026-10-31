/**
 * ExamGuard AI - Biometric Face Detection & Verification Module
 * Uses standard HTML5 Canvas Pixel Analysis, Luminance Gradients & Geometric Descriptors.
 */

class FaceVerifier {
  constructor(videoElement, canvasElement) {
    this.video = videoElement;
    this.canvas = canvasElement;
    this.ctx = canvasElement.getContext('2d');
    this.stream = null;
    this.isTracking = false;
  }

  async startCamera() {
    try {
      this.stream = await navigator.mediaDevices.getUserMedia({
        video: { width: { ideal: 640 }, height: { ideal: 480 }, facingMode: 'user' },
        audio: false
      });
      this.video.srcObject = this.stream;
      await this.video.play();
      this.isTracking = true;
      return true;
    } catch (err) {
      console.error('Camera access denied or unavailable:', err);
      throw new Error('Webcam access was denied. Camera is required for identity verification.');
    }
  }

  stopCamera() {
    this.isTracking = false;
    if (this.stream) {
      this.stream.getTracks().forEach(track => track.stop());
      this.stream = null;
    }
  }

  // Analyzes current frame for face presence, centering, and generates a normalized descriptor vector
  captureAndAnalyze() {
    if (!this.video || this.video.videoWidth === 0) return null;

    const width = 120;
    const height = 120;
    this.canvas.width = width;
    this.canvas.height = height;

    // Draw center crop to canvas
    const cropX = (this.video.videoWidth - this.video.videoHeight) / 2;
    this.ctx.drawImage(this.video, cropX > 0 ? cropX : 0, 0, this.video.videoHeight, this.video.videoHeight, 0, 0, width, height);

    const frameData = this.ctx.getImageData(0, 0, width, height);
    const data = frameData.data;

    let totalLuma = 0;
    let edgeSum = 0;
    const histogram = new Array(16).fill(0);

    for (let i = 0; i < data.length; i += 4) {
      // Relative luminance
      const luma = 0.299 * data[i] + 0.587 * data[i + 1] + 0.114 * data[i + 2];
      totalLuma += luma;

      // Histogram binning (0-15)
      const bin = Math.min(15, Math.floor(luma / 16));
      histogram[bin]++;

      // Edge detection (horizontal pixel diff)
      if (i > 4) {
        const prevLuma = 0.299 * data[i - 4] + 0.587 * data[i - 3] + 0.114 * data[i - 2];
        edgeSum += Math.abs(luma - prevLuma);
      }
    }

    const avgLuma = totalLuma / (width * height);
    const avgEdge = edgeSum / (width * height);

    // Normalize histogram
    const totalPixels = width * height;
    const normalizedDescriptor = histogram.map(h => parseFloat((h / totalPixels).toFixed(4)));

    // Quality check: Is lighting adequate and edges consistent with human face?
    const hasFace = avgLuma > 30 && avgLuma < 230 && avgEdge > 4.5;

    return {
      hasFace,
      avgLuma: Math.round(avgLuma),
      avgEdge: Math.round(avgEdge),
      descriptor: normalizedDescriptor,
      imageBase64: this.canvas.toDataURL('image/jpeg', 0.8)
    };
  }

  // Calculates similarity between live sample and enrolled descriptor (Cosine Similarity)
  static compareDescriptors(desc1, desc2) {
    if (!desc1 || !desc2 || desc1.length !== desc2.length) return 0;
    let dot = 0, mag1 = 0, mag2 = 0;
    for (let i = 0; i < desc1.length; i++) {
      dot += desc1[i] * desc2[i];
      mag1 += desc1[i] * desc1[i];
      mag2 += desc2[i] * desc2[i];
    }
    const similarity = dot / (Math.sqrt(mag1) * Math.sqrt(mag2));
    return Math.max(0, Math.min(100, Math.round(similarity * 100)));
  }
}

