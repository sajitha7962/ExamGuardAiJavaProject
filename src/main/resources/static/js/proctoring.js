/**
 * ExamGuard AI - AI-Assisted Proctoring Engine
 * Monitored APIs: Page Visibility, Fullscreen, MediaDevices, Screen Capture, WebSocket Alerts
 */

class ProctoringEngine {
  constructor(config) {
    this.attemptId = config.attemptId;
    this.examId = config.examId;
    this.maxWarnings = config.maxWarnings || 5;
    this.currentWarnings = 0;
    this.ws = null;
    this.faceVerifier = null;
    this.onWarningIssued = config.onWarningIssued || (() => {});
    this.onAutoSubmitTriggered = config.onAutoSubmitTriggered || (() => {});
    this.isMonitoring = false;
    this.checkInterval = null;
  }

  async init(videoElement, canvasElement) {
    this.faceVerifier = new FaceVerifier(videoElement, canvasElement);
    await this.faceVerifier.startCamera();
    this.setupListeners();
    this.initWebSocket();
    this.startPeriodicChecks();
    this.isMonitoring = true;
  }

  setupListeners() {
    // 1. Tab switch / Page Visibility detection
    document.addEventListener('visibilitychange', () => {
      if (!this.isMonitoring) return;
      if (document.hidden) {
        this.reportEvent('TAB_SWITCH', 'HIGH', 'Student switched browser tab or minimized examination window.');
      }
    });

    // 2. Window Blur detection
    window.addEventListener('blur', () => {
      if (!this.isMonitoring) return;
      this.reportEvent('TAB_SWITCH', 'WARNING', 'Examination window lost focus.');
    });

    // 3. Fullscreen Exit detection
    document.addEventListener('fullscreenchange', () => {
      if (!this.isMonitoring) return;
      if (!document.fullscreenElement) {
        this.reportEvent('FULLSCREEN_EXIT', 'HIGH', 'Student exited fullscreen examination mode.');
      }
    });
  }

  async requestFullscreen() {
    try {
      if (!document.fullscreenElement) {
        await document.documentElement.requestFullscreen();
      }
    } catch (err) {
      console.warn('Fullscreen request bypassed or failed:', err);
    }
  }

  async requestScreenShare() {
    try {
      const stream = await navigator.mediaDevices.getDisplayMedia({
        video: { cursor: "always" },
        audio: false
      });
      stream.getVideoTracks()[0].addEventListener('ended', () => {
        this.reportEvent('SCREEN_SHARE_STOPPED', 'HIGH', 'Screen sharing stream was manually terminated by student.');
      });
      return stream;
    } catch (err) {
      this.reportEvent('SCREEN_SHARE_STOPPED', 'HIGH', 'Screen share authorization was denied.');
      throw err;
    }
  }

  initWebSocket() {
    try {
      const protocol = window.location.protocol === 'https:' ? 'wss:' : 'ws:';
      const wsUrl = `${protocol}//${window.location.host}/ws/proctoring`;
      this.ws = new WebSocket(wsUrl);

      this.ws.onopen = () => {
        this.ws.send(JSON.stringify({
          type: 'REGISTER_STUDENT',
          attemptId: this.attemptId,
          examId: this.examId
        }));
      };

      this.ws.onmessage = (event) => {
        try {
          const msg = JSON.parse(event.data);
          if (msg.type === 'PROFESSOR_ACTION' && msg.attemptId === this.attemptId) {
            if (msg.action === 'WARNING') {
              this.issueWarning(msg.message || 'Warning issued by supervising professor.');
            } else if (msg.action === 'TERMINATE') {
              this.onAutoSubmitTriggered('Terminated by professor.');
            }
          }
        } catch (e) {}
      };
    } catch (e) {
      console.warn('Proctoring WebSocket unavailable, falling back to REST log.', e);
    }
  }

  startPeriodicChecks() {
    this.checkInterval = setInterval(() => {
      if (!this.isMonitoring) return;

      const analysis = this.faceVerifier.captureAndAnalyze();
      if (analysis) {
        if (!analysis.hasFace) {
          this.reportEvent('FACE_ABSENT', 'WARNING', 'No distinct face detected in camera viewport.');
        }

        // Send periodic heartbeat over WebSocket
        if (this.ws && this.ws.readyState === WebSocket.OPEN) {
          this.ws.send(JSON.stringify({
            type: 'HEARTBEAT',
            attemptId: this.attemptId,
            examId: this.examId,
            faceDetected: analysis.hasFace,
            timestamp: new Date().toISOString()
          }));
        }
      }
    }, 7000);
  }

  async reportEvent(eventType, severity, description) {
    this.currentWarnings++;
    this.issueWarning(`${description} (Warning ${this.currentWarnings} of ${this.maxWarnings})`);

    // 1. Report to Backend API
    try {
      await apiRequest('/student/proctoring/event', 'POST', {
        attemptId: this.attemptId,
        examId: this.examId,
        eventType,
        severity,
        description
      });
    } catch (e) {
      console.error('Failed to log proctoring event to backend:', e);
    }

    // 2. Broadcast via WebSocket to Live Monitoring Professor
    if (this.ws && this.ws.readyState === WebSocket.OPEN) {
      this.ws.send(JSON.stringify({
        type: 'PROCTORING_ALERT',
        attemptId: this.attemptId,
        examId: this.examId,
        eventType,
        severity,
        description,
        warningCount: this.currentWarnings
      }));
    }

    // 3. Trigger auto submit if violation limit exceeded
    if (this.currentWarnings >= this.maxWarnings) {
      this.isMonitoring = false;
      this.onAutoSubmitTriggered('Maximum proctoring warnings exceeded. Examination submitted automatically.');
    }
  }

  issueWarning(message) {
    this.onWarningIssued(message, this.currentWarnings, this.maxWarnings);
    showToast(`⚠️ PROCTORING ALERT: ${message}`, true);
  }

  stop() {
    this.isMonitoring = false;
    if (this.checkInterval) clearInterval(this.checkInterval);
    if (this.faceVerifier) this.faceVerifier.stopCamera();
    if (this.ws) this.ws.close();
  }
}

