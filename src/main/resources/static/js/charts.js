/**
 * ExamGuard AI - Native Responsive Charting Engine
 * Uses pure HTML5 Canvas conforming strictly to the 4 approved colors:
 * Deep Teal (#287A74), Ocean Teal (#55A9A0), Mint Green (#AEEED3), Soft Yellow (#FFF8B0)
 */

class GuardChart {
  static drawBarChart(canvasId, labels, data, title = '') {
    const canvas = document.getElementById(canvasId);
    if (!canvas) return;
    const ctx = canvas.getContext('2d');
    const width = canvas.width = canvas.parentElement.clientWidth || 500;
    const height = canvas.height = 240;

    ctx.clearRect(0, 0, width, height);

    if (!data || data.length === 0) {
      ctx.fillStyle = '#287A74';
      ctx.font = '14px sans-serif';
      ctx.textAlign = 'center';
      ctx.fillText('No data available', width / 2, height / 2);
      return;
    }

    const padding = 45;
    const chartWidth = width - padding * 2;
    const chartHeight = height - padding * 2;
    const maxVal = Math.max(...data, 10);
    const barWidth = Math.min(50, (chartWidth / data.length) * 0.65);
    const gap = chartWidth / data.length;

    // Grid line
    ctx.strokeStyle = '#AEEED3';
    ctx.lineWidth = 1;
    ctx.beginPath();
    ctx.moveTo(padding, height - padding);
    ctx.lineTo(width - padding, height - padding);
    ctx.stroke();

    // Bars
    data.forEach((val, idx) => {
      const barH = (val / maxVal) * chartHeight;
      const x = padding + idx * gap + (gap - barWidth) / 2;
      const y = height - padding - barH;

      // Color alternating using approved palette
      ctx.fillStyle = idx % 2 === 0 ? '#287A74' : '#55A9A0';
      ctx.beginPath();
      ctx.roundRect(x, y, barWidth, barH, [4, 4, 0, 0]);
      ctx.fill();

      // Value label
      ctx.fillStyle = '#184743';
      ctx.font = 'bold 11px sans-serif';
      ctx.textAlign = 'center';
      ctx.fillText(val, x + barWidth / 2, y - 6);

      // X Axis Label
      const shortLabel = labels[idx] ? (labels[idx].length > 10 ? labels[idx].substring(0, 8) + '..' : labels[idx]) : '';
      ctx.font = '11px sans-serif';
      ctx.fillStyle = '#287A74';
      ctx.fillText(shortLabel, x + barWidth / 2, height - padding + 16);
    });
  }

  static drawDonutChart(canvasId, labels, data) {
    const canvas = document.getElementById(canvasId);
    if (!canvas) return;
    const ctx = canvas.getContext('2d');
    const width = canvas.width = 240;
    const height = canvas.height = 240;
    const cx = width / 2;
    const cy = height / 2;
    const radius = 80;
    const innerRadius = 45;

    ctx.clearRect(0, 0, width, height);

    const total = data.reduce((a, b) => a + b, 0);
    if (total === 0) return;

    const colors = ['#287A74', '#55A9A0', '#AEEED3', '#FFF8B0', '#1c5551'];
    let startAngle = -Math.PI / 2;

    data.forEach((val, i) => {
      if (val === 0) return;
      const sliceAngle = (val / total) * 2 * Math.PI;

      ctx.beginPath();
      ctx.arc(cx, cy, radius, startAngle, startAngle + sliceAngle);
      ctx.arc(cx, cy, innerRadius, startAngle + sliceAngle, startAngle, true);
      ctx.closePath();
      ctx.fillStyle = colors[i % colors.length];
      ctx.fill();

      startAngle += sliceAngle;
    });

    // Center text
    ctx.fillStyle = '#287A74';
    ctx.font = 'bold 16px sans-serif';
    ctx.textAlign = 'center';
    ctx.textBaseline = 'middle';
    ctx.fillText(`${total}`, cx, cy);
  }
}

