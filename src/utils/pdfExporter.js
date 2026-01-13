import jsPDF from "jspdf";

// --- 配置项 ---
const FONT_URL = '/fonts/SourceHanSansCN-Regular.ttf';
const FONT_NAME = 'SourceHanSansCN';

// --- 样式常量 ---
const COLORS = {
  PRIMARY: [197, 160, 89],
  PRIMARY_DARK: [168, 132, 66],
  ACCENT: [231, 212, 168],
  BG: [249, 248, 244],
  HEADER_BG: [251, 247, 239],
  BG_GRAY: [246, 246, 246],
  TEXT_MAIN: [51, 51, 51],
  TEXT_LIGHT: [87, 83, 78],
  BORDER: [231, 229, 228],
  BORDER_STRONG: [205, 198, 191],
  BORDER_SOFT: [227, 222, 217],
  WHITE: [255, 255, 255],
  HEADER_TEXT: [51, 51, 51]
};

// 布局常量
const MARGIN = 16;
const PAGE_HEIGHT = 297;
const PAGE_WIDTH = 210;
const CONTENT_WIDTH = PAGE_WIDTH - MARGIN * 2;

/**
 * 加载字体辅助函数
 */
async function loadFontAsBase64(url) {
  try {
    const response = await fetch(url);
    if (!response.ok) throw new Error("Font fetch failed");
    const blob = await response.blob();
    return new Promise((resolve, reject) => {
      const reader = new FileReader();
      reader.onloadend = () => resolve(reader.result.split(',')[1]);
      reader.onerror = reject;
      reader.readAsDataURL(blob);
    });
  } catch (error) {
    console.error('字体加载失败:', error);
    return null;
  }
}

/**
 * 导出 PDF 主函数
 */
export const exportSchoolReport = async (studentInfo, matchedSchools) => {
  const doc = new jsPDF();

  // 1. 加载并注册字体
  const fontBase64 = await loadFontAsBase64(FONT_URL);
  const ACTIVE_FONT = fontBase64 ? FONT_NAME : 'helvetica';
  if (fontBase64) {
    doc.addFileToVFS(`${FONT_NAME}.ttf`, fontBase64);
    doc.addFont(`${FONT_NAME}.ttf`, FONT_NAME, "normal");
    doc.addFont(`${FONT_NAME}.ttf`, FONT_NAME, "bold"); // jsPDF bold 模拟
    doc.setFont(FONT_NAME, 'normal');
  } else {
    alert("字体加载失败，中文将无法显示，请检查 public/fonts/SourceHanSansCN-Regular.ttf");
    // 继续执行，但中文会乱码
  }
  const setFontStyle = (style = 'normal') => doc.setFont(ACTIVE_FONT, style);

  // --- 状态管理 ---
  // 使用 cursor 对象在闭包中传递当前的 Y 坐标
  const cursor = { y: MARGIN };

  // --- 内部绘图辅助函数 ---

  // 检查是否需要换页
  const checkPageBreak = (heightToAdd) => {
    if (cursor.y + heightToAdd > PAGE_HEIGHT - MARGIN) {
      doc.addPage();
      cursor.y = MARGIN;
      return true;
    }
    return false;
  };

  const drawSectionHeader = (title, enTitle) => {
    // 预留空间，避免标题在页面底部
    if (checkPageBreak(16)) cursor.y += 0;

    // 中文标题
    doc.setFontSize(12);
    doc.setTextColor(...COLORS.PRIMARY_DARK);
    setFontStyle('bold');
    doc.text(title, MARGIN, cursor.y + 9.6);

    // 英文标题
    if (enTitle) {
      const zhWidth = doc.getTextWidth(title);
      doc.setFontSize(9);
      doc.setTextColor(...COLORS.TEXT_LIGHT);
      setFontStyle('normal');
      doc.text(enTitle, MARGIN + zhWidth + 2, cursor.y + 9.6);
    }

    cursor.y += 12;
  };

  // 绘制文本段落
  const drawParagraph = (text, fontSize = 10, color = COLORS.TEXT_MAIN) => {
    if (!text) return;

    doc.setFontSize(fontSize);
    doc.setTextColor(...color);
    setFontStyle('normal');

    // 分割段落
    const paragraphs = text.split('\n');

    for (let p of paragraphs) {
      if (!p.trim()) continue;

      // 自动换行计算
      const lines = doc.splitTextToSize(p.trim(), CONTENT_WIDTH);
      const lineHeight = fontSize * 0.3527 + 6.2;
      const blockHeight = lines.length * lineHeight;

      // 换页检查
      if (checkPageBreak(blockHeight)) {
        doc.setFontSize(fontSize); // 换页后重置样式
        doc.setTextColor(...color);
      }

      doc.text(lines, MARGIN, cursor.y + fontSize * 0.4);
      cursor.y += blockHeight + 4;
    }
    cursor.y += 3;
  };


  // 绘制核心课程表格
  const drawCoreCoursesTable = (courses, startY) => {
    if (!courses || courses.length === 0) return startY;

    const headerHeight = 10;
    const col1 = CONTENT_WIDTH * 0.10;
    const col2 = CONTENT_WIDTH * 0.45;
    const col3 = CONTENT_WIDTH * 0.15;
    const col4 = CONTENT_WIDTH * 0.15;
    const col5 = CONTENT_WIDTH - col1 - col2 - col3 - col4;

    const x1 = MARGIN;
    const x2 = x1 + col1;
    const x3 = x2 + col2;
    const x4 = x3 + col3;
    const x5 = x4 + col4;

    const rowFontSize = 9;
    const padX = 2.5;
    const rows = courses.map((course, index) => {
      let nameText = course.courseName || '';
      if (course.courseEnglishName) nameText += `\n${course.courseEnglishName}`;
      doc.setFontSize(rowFontSize);
      const nameLines = doc.splitTextToSize(nameText, col2 - padX * 2);
      const rowHeight = Math.max(11, nameLines.length * 4.2 + 4.8);
      return {
        index: String(index + 1),
        nameLines,
        courseType: course.courseType || '—',
        credits: String(course.credits ?? '—'),
        semester: course.semester ? `第${course.semester}学期` : '—',
        rowHeight
      };
    });

    const drawSegment = (segmentY, segmentRows) => {
      const segmentHeight = headerHeight + segmentRows.reduce((sum, r) => sum + r.rowHeight, 0);
      doc.setFillColor(...COLORS.WHITE);
      doc.setDrawColor(...COLORS.BORDER_STRONG);
      doc.setLineWidth(0.35);
      doc.roundedRect(MARGIN, segmentY, CONTENT_WIDTH, segmentHeight, 8, 8, 'FD');

      doc.setTextColor(...COLORS.TEXT_LIGHT);
      doc.setFontSize(9);
      setFontStyle('bold');
      const headerTextY = segmentY + 6.6;
      doc.text("序号", x1 + padX, headerTextY);
      doc.text("课程名称 / Course Name", x2 + padX, headerTextY);
      doc.text("类型", x3 + padX, headerTextY);
      doc.text("学分", x4 + padX, headerTextY);
      doc.text("学期", x5 + padX, headerTextY);

      doc.setDrawColor(...COLORS.BORDER_SOFT);
      doc.setLineWidth(0.25);
      doc.line(MARGIN, segmentY + headerHeight, MARGIN + CONTENT_WIDTH, segmentY + headerHeight);

      doc.setTextColor(...COLORS.TEXT_MAIN);
      doc.setFontSize(rowFontSize);
      setFontStyle('normal');

      let y = segmentY + headerHeight;
      segmentRows.forEach((r) => {
        const rowY = y + 4.2;
        doc.text(r.index, x1 + padX, rowY + 1.8);
        doc.text(r.nameLines, x2 + padX, rowY + 1.8);
        doc.text(r.courseType, x3 + padX, rowY + 1.8);
        doc.text(r.credits, x4 + padX, rowY + 1.8);
        doc.text(r.semester, x5 + padX, rowY + 1.8);

        y += r.rowHeight;
        doc.line(MARGIN, y, MARGIN + CONTENT_WIDTH, y);
      });

      return segmentY + segmentHeight;
    };

    let y = startY;
    let idx = 0;

    while (idx < rows.length) {
      const remaining = PAGE_HEIGHT - MARGIN - y;
      if (remaining < headerHeight + 14) {
        doc.addPage();
        y = MARGIN;
      }

      const segmentRows = [];
      let used = headerHeight;
      while (idx < rows.length) {
        const next = rows[idx];
        if (used + next.rowHeight > PAGE_HEIGHT - MARGIN - y) break;
        segmentRows.push(next);
        used += next.rowHeight;
        idx += 1;
      }

      if (segmentRows.length === 0) {
        doc.addPage();
        y = MARGIN;
        continue;
      }

      y = drawSegment(y, segmentRows);
      y += 8;
    }

    return y;
  };

  // 绘制通用表格 (用于关键数据和专业介绍)
  const drawCommonTable = (data, startY) => {
    const headerHeight = 0; // 无表头
    let y = startY;

    doc.setDrawColor(...COLORS.BORDER_STRONG);
    doc.setLineWidth(0.45);

    data.forEach((item, index) => {
      const rawLabel = String(item?.label ?? '').trim();
      const hasLabel = rawLabel.length > 0;
      const labelWidth = hasLabel ? 32 : 0;
      const labelInnerPad = 6;
      const contentLeftPad = hasLabel ? 10 : 8;
      const rightPad = 6;
      const labelX = MARGIN + labelInnerPad;
      const contentX = MARGIN + labelWidth + contentLeftPad;
      const contentWidth = CONTENT_WIDTH - (contentX - MARGIN) - rightPad;

      const rawValue = item?.value ?? '';
      const normalizedValue = String(rawValue)
        .replace(/\r\n/g, '\n')
        .replace(/\r/g, '\n')
        .replace(/\u00A0/g, ' ')
        .trim();
      const paragraphs = normalizedValue
        ? normalizedValue.split(/\n{2,}/).map((p) => p.trim()).filter(Boolean)
        : ['—'];

      const labelFontSize = 10;
      const labelLineHeight = 5.6;
      const bodyFontSize = 9.6;
      const bodyLineHeight = 5.8;
      const paragraphGap = 2.2;
      const firstLineIndent = 4;
      const topPad = 7.2;
      const bottomPad = 7.2;
      const minRowHeight = 22;

      doc.setFontSize(labelFontSize);
      const labelText = hasLabel ? `${rawLabel}：` : '';
      const labelLines = hasLabel
        ? doc.splitTextToSize(labelText, Math.max(10, labelWidth - labelInnerPad))
        : [];

      doc.setFontSize(bodyFontSize);
      const paraLayouts = paragraphs.map((p) => {
        const lines = doc.splitTextToSize(p, Math.max(10, contentWidth - firstLineIndent));
        return { lines };
      });

      const labelHeight = hasLabel ? labelLines.length * labelLineHeight : 0;
      const tokens = [];
      paraLayouts.forEach((para, paraIndex) => {
        para.lines.forEach((line, lineIndex) => {
          tokens.push({ type: 'line', text: String(line), indent: lineIndex === 0 ? firstLineIndent : 0 });
        });
        if (paraIndex < paraLayouts.length - 1) tokens.push({ type: 'gap', height: paragraphGap });
      });

      const ensureNewPage = () => {
        const height = y - startY;
        if (height > 0.5) {
          doc.setDrawColor(...COLORS.BORDER_STRONG);
          doc.setLineWidth(0.45);
          doc.roundedRect(MARGIN, startY, CONTENT_WIDTH, height, 8, 8, 'S');
        }
        doc.addPage();
        y = MARGIN;
        startY = MARGIN;
      };

      let tokenIndex = 0;
      let isFirstSegment = true;
      while (tokenIndex < tokens.length) {
        const remaining = PAGE_HEIGHT - MARGIN - y;
        const maxInnerHeight = remaining - topPad - bottomPad;
        const labelHeightThis = isFirstSegment ? labelHeight : 0;

        if (maxInnerHeight <= Math.max(labelHeightThis, bodyLineHeight)) {
          ensureNewPage();
          continue;
        }

        let contentHeightThis = 0;
        let endIndex = tokenIndex;
        while (endIndex < tokens.length) {
          const t = tokens[endIndex];
          const h = t.type === 'line' ? bodyLineHeight : t.height;
          if (contentHeightThis + h > maxInnerHeight && contentHeightThis > 0) break;
          if (contentHeightThis + h > maxInnerHeight && contentHeightThis === 0) break;
          contentHeightThis += h;
          endIndex += 1;
        }

        while (endIndex > tokenIndex && tokens[endIndex - 1].type === 'gap') {
          contentHeightThis -= tokens[endIndex - 1].height;
          endIndex -= 1;
        }

        if (endIndex === tokenIndex) {
          if (y !== MARGIN) {
            ensureNewPage();
            continue;
          }
          endIndex = Math.min(tokens.length, tokenIndex + 1);
          contentHeightThis = tokens[tokenIndex].type === 'line' ? bodyLineHeight : 0;
        }

        const innerHeightThis = Math.max(labelHeightThis, contentHeightThis);
        const rowHeightThis = Math.max(minRowHeight, topPad + innerHeightThis + bottomPad);
        if (rowHeightThis > remaining && y !== MARGIN) {
          ensureNewPage();
          continue;
        }

        doc.setTextColor(...COLORS.TEXT_MAIN);
        if (isFirstSegment && hasLabel) {
          setFontStyle('bold');
          doc.setFontSize(labelFontSize);
          const labelTextY = y + topPad + labelFontSize * 0.3527;
          doc.text(labelLines, labelX, labelTextY);
        }

        setFontStyle('normal');
        doc.setFontSize(bodyFontSize);
        let contentY = y + topPad + bodyFontSize * 0.3527;
        for (let i = tokenIndex; i < endIndex; i++) {
          const t = tokens[i];
          if (t.type === 'gap') {
            contentY += t.height;
            continue;
          }
          doc.text(t.text, contentX + (t.indent || 0), contentY);
          contentY += bodyLineHeight;
        }

        y += rowHeightThis;
        tokenIndex = endIndex;
        isFirstSegment = false;
      }

      // 分隔线 (最后一行不需要，外框会处理)
      if (index < data.length - 1) {
        doc.setDrawColor(COLORS.BORDER_SOFT[0], COLORS.BORDER_SOFT[1], COLORS.BORDER_SOFT[2]);
        doc.line(MARGIN + 4, y, MARGIN + CONTENT_WIDTH - 4, y);
        doc.setDrawColor(...COLORS.BORDER_STRONG);
      }
    });

    // 闭合外边框
    doc.setDrawColor(...COLORS.BORDER_STRONG);
    doc.setLineWidth(0.45);
    doc.roundedRect(MARGIN, startY, CONTENT_WIDTH, y - startY, 8, 8, 'S');

    return y + 8;
  };

  const drawApplyGridTable = (groups, startY, columns = 3) => {
    if (!groups || groups.length === 0) return startY;

    const colWidth = CONTENT_WIDTH / columns;
    const padX = 5;
    const labelFontSize = 9;
    const valueFontSize = 10;
    const labelRowHeight = 10;
    const minValueRowHeight = 12;
    const valueLineHeight = 6.4;

    const measured = groups.map((group) => {
      const normalized = Array.from({ length: columns }).map((_, i) => {
        const cell = group[i] || { label: '', value: '' };
        const label = String(cell.label ?? '');
        const value = String(cell.value ?? '');
        doc.setFontSize(labelFontSize);
        const labelLines = label ? doc.splitTextToSize(label, colWidth - padX * 2) : [''];
        doc.setFontSize(valueFontSize);
        const valueLines = value ? doc.splitTextToSize(value, colWidth - padX * 2) : ['—'];
        return { labelLines, valueLines };
      });

      const maxValueLines = Math.max(...normalized.map((c) => c.valueLines.length));
      const valueRowHeight = Math.max(minValueRowHeight, maxValueLines * valueLineHeight + 6);
      const groupHeight = labelRowHeight + valueRowHeight;
      return { normalized, groupHeight, valueRowHeight };
    });

    const drawSegment = (segmentY, segmentRows) => {
      const inset = 1.2;
      const segmentHeight = segmentRows.reduce((sum, r) => sum + r.groupHeight, 0);
      doc.setFillColor(...COLORS.WHITE);
      doc.setDrawColor(...COLORS.BORDER_STRONG);
      doc.setLineWidth(0.45);
      doc.roundedRect(MARGIN, segmentY, CONTENT_WIDTH, segmentHeight, 8, 8, 'FD');

      doc.setDrawColor(...COLORS.BORDER_SOFT);
      doc.setLineWidth(0.3);

      let y = segmentY;
      if (columns > 1) {
        for (let c = 1; c < columns; c++) {
          const x = MARGIN + c * colWidth;
          doc.line(x, segmentY + inset, x, segmentY + segmentHeight - inset);
        }
      }
      segmentRows.forEach((row, rowIndex) => {
        doc.setTextColor(...COLORS.TEXT_LIGHT);
        doc.setFontSize(labelFontSize);
        setFontStyle('bold');
        for (let c = 0; c < columns; c++) {
          const x = MARGIN + c * colWidth;
          doc.text(row.normalized[c].labelLines, x + padX, y + 6.7);
        }

        doc.setTextColor(...COLORS.TEXT_MAIN);
        doc.setFontSize(valueFontSize);
        setFontStyle('normal');
        const valueY = y + labelRowHeight;
        for (let c = 0; c < columns; c++) {
          const x = MARGIN + c * colWidth;
          doc.text(row.normalized[c].valueLines, x + padX, valueY + 7.4);
        }

        y += row.groupHeight;
        if (rowIndex < segmentRows.length - 1) {
          doc.setDrawColor(...COLORS.BORDER_SOFT);
          doc.line(MARGIN + inset, y, MARGIN + CONTENT_WIDTH - inset, y);
        }
      });

      return segmentY + segmentHeight;
    };

    let y = startY;
    let idx = 0;
    while (idx < measured.length) {
      const remaining = PAGE_HEIGHT - MARGIN - y;
      if (remaining < 18) {
        doc.addPage();
        y = MARGIN;
      }

      const segmentRows = [];
      let used = 0;
      while (idx < measured.length) {
        const next = measured[idx];
        if (used + next.groupHeight > PAGE_HEIGHT - MARGIN - y) break;
        segmentRows.push(next);
        used += next.groupHeight;
        idx += 1;
      }

      if (segmentRows.length === 0) {
        doc.addPage();
        y = MARGIN;
        continue;
      }

      y = drawSegment(y, segmentRows);
      y += 8;
    }

    return y;
  };

  // 绘制关键数据网格 (改用表格形式，支持多列)
  const drawKeyStats = (stats) => {
    const groups = [];
    for (let i = 0; i < stats.length; i += 3) {
      groups.push(stats.slice(i, i + 3).map((s) => ({ label: s.label, value: s.value })));
    }
    return drawApplyGridTable(groups, cursor.y, 3);
  };


  // 绘制录取要求表格 (蓝色表头风格)
  const drawRequirementsTable = (reqs) => {
    const headerHeight = 10;

    // 如果剩余空间太小，直接换页
    if (cursor.y + 60 > PAGE_HEIGHT - MARGIN) {
      doc.addPage();
      cursor.y = MARGIN;
    }

    const startY = cursor.y;

    // 1. 表头背景
    // 表头文字
    doc.setTextColor(...COLORS.TEXT_LIGHT);
    doc.setFontSize(10);
    setFontStyle('bold');
    doc.text("录取要求 / Admission Requirements", MARGIN + 4, cursor.y + 7);

    cursor.y += headerHeight;

    // 2. 表格内容
    doc.setDrawColor(...COLORS.BORDER_STRONG);
    doc.setLineWidth(0.45);

    reqs.forEach((req, index) => {
      doc.setFontSize(10);
      const labelWidth = 35;
      const contentWidth = CONTENT_WIDTH - labelWidth - 8;

      const contentLines = doc.splitTextToSize(req.value, contentWidth);
      const lineHeight = 6.0;
      const rowHeight = Math.max(14, contentLines.length * lineHeight + 9);

      // 行内分页处理
      if (cursor.y + rowHeight > PAGE_HEIGHT - MARGIN) {
        // 闭合上一页的边框
        doc.setDrawColor(...COLORS.BORDER_STRONG);
        doc.setLineWidth(0.45);
        doc.roundedRect(MARGIN, startY, CONTENT_WIDTH, cursor.y - startY, 8, 8, 'S');
        doc.addPage();
        cursor.y = MARGIN;
        // 新页面画顶线
        doc.line(MARGIN, cursor.y, MARGIN + CONTENT_WIDTH, cursor.y);
      }

      // Label
      doc.setTextColor(...COLORS.TEXT_MAIN);
      setFontStyle('bold');
      doc.text(req.label + "：", MARGIN + 4, cursor.y + 8);

      // Content
      setFontStyle('normal');
      doc.text(contentLines, MARGIN + labelWidth + 2, cursor.y + 8);

      // 分隔线 (最后一行不需要)
      if (index < reqs.length - 1) {
        doc.setDrawColor(...COLORS.BORDER_SOFT);
        doc.line(MARGIN + 2, cursor.y + rowHeight, MARGIN + CONTENT_WIDTH - 2, cursor.y + rowHeight);
        doc.setDrawColor(...COLORS.BORDER_STRONG);
      }

      cursor.y += rowHeight;
    });

    // 闭合外边框
    doc.setDrawColor(...COLORS.BORDER_STRONG);
    doc.setLineWidth(0.45);
    doc.roundedRect(MARGIN, startY, CONTENT_WIDTH, cursor.y - startY, 8, 8, 'S');

    cursor.y += 10;
  };

  // 绘制汇总表格
  const drawSummaryTable = (schools, startY) => {
    const headerHeight = 10;
    const col0 = CONTENT_WIDTH * 0.08;
    const col1 = CONTENT_WIDTH * 0.32;
    const col2 = CONTENT_WIDTH * 0.38;
    const col3 = CONTENT_WIDTH * 0.12;
    const col4 = CONTENT_WIDTH - col0 - col1 - col2 - col3;

    const x1 = MARGIN;
    const x2 = x1 + col0;
    const x3 = x2 + col1;
    const x4 = x3 + col2;
    const x5 = x4 + col3;

    const rows = schools.map((school, index) => {
      const schoolName = school.name || '-';
      const majorName = school.major || school.recommendedMajor || '-';
      const rank = school.rank || 'N/A';
      const duration = school.duration || '1年';
      const seqText = `${index + 1}.`;

      doc.setFontSize(9.2);
      const schoolLines = doc.splitTextToSize(schoolName, col1 - 6);
      const majorLines = doc.splitTextToSize(majorName, col2 - 6);
      const lines = Math.max(schoolLines.length, majorLines.length);
      const rowHeight = Math.max(10, lines * 5 + 3.5);

      return { seqText, schoolLines, majorLines, rank: String(rank), duration, rowHeight };
    });

    const drawSegment = (segmentY, segmentRows) => {
      const inset = 1.2;
      const segmentHeight = headerHeight + segmentRows.reduce((sum, r) => sum + r.rowHeight, 0);

      doc.setFillColor(...COLORS.WHITE);
      doc.setDrawColor(...COLORS.BORDER_STRONG);
      doc.setLineWidth(0.45);
      doc.roundedRect(MARGIN, segmentY, CONTENT_WIDTH, segmentHeight, 8, 8, 'FD');

      doc.setTextColor(...COLORS.TEXT_LIGHT);
      doc.setFontSize(9.5);
      setFontStyle('bold');
      const headerTextY = segmentY + 6.6;
      doc.text("序号", x1 + 2.5, headerTextY);
      doc.text("学校 / School", x2 + 2.5, headerTextY);
      doc.text("专业 / Major", x3 + 2.5, headerTextY);
      doc.text("专业QS排名", x4 + 2.5, headerTextY);
      doc.text("学制", x5 + 2.5, headerTextY);

      doc.setDrawColor(...COLORS.BORDER_SOFT);
      doc.setLineWidth(0.3);
      doc.line(MARGIN + inset, segmentY + headerHeight, MARGIN + CONTENT_WIDTH - inset, segmentY + headerHeight);
      doc.line(x2, segmentY + inset, x2, segmentY + segmentHeight - inset);
      doc.line(x3, segmentY + inset, x3, segmentY + segmentHeight - inset);
      doc.line(x4, segmentY + inset, x4, segmentY + segmentHeight - inset);
      doc.line(x5, segmentY + inset, x5, segmentY + segmentHeight - inset);

      let y = segmentY + headerHeight;
      doc.setTextColor(...COLORS.TEXT_MAIN);
      setFontStyle('normal');
      doc.setFontSize(9.2);

      segmentRows.forEach((r, rowIdx) => {
        const rowY = y + 3.6;
        doc.text(r.seqText, x1 + 2.5, rowY + 2);
        doc.text(r.schoolLines, x2 + 2.5, rowY + 2);
        doc.text(r.majorLines, x3 + 2.5, rowY + 2);
        doc.text(r.rank, x4 + 2.5, rowY + 2);
        doc.text(r.duration, x5 + 2.5, rowY + 2);

        y += r.rowHeight;
        if (rowIdx < segmentRows.length - 1) {
          doc.line(MARGIN + inset, y, MARGIN + CONTENT_WIDTH - inset, y);
        }
      });

      return segmentY + segmentHeight;
    };

    let y = startY;
    let idx = 0;

    while (idx < rows.length) {
      const remaining = PAGE_HEIGHT - MARGIN - y;
      if (remaining < headerHeight + 12) {
        doc.addPage();
        y = MARGIN;
      }

      const segmentRows = [];
      let used = headerHeight;

      while (idx < rows.length) {
        const next = rows[idx];
        if (used + next.rowHeight > PAGE_HEIGHT - MARGIN - y) break;
        segmentRows.push(next);
        used += next.rowHeight;
        idx += 1;
      }

      if (segmentRows.length === 0) {
        doc.addPage();
        y = MARGIN;
        continue;
      }

      y = drawSegment(y, segmentRows);
    }

    return y;
  };

  // --- 2. 封面页生成 ---
  const bannerHeight = 46;
  doc.setFillColor(...COLORS.BG);
  doc.rect(0, 0, PAGE_WIDTH, PAGE_HEIGHT, 'F');
  doc.setFillColor(...COLORS.HEADER_BG);
  doc.rect(0, 0, PAGE_WIDTH, bannerHeight, 'F');
  doc.setTextColor(...COLORS.TEXT_MAIN);
  setFontStyle('bold');
  doc.setFontSize(22);
  doc.text("智能选校分析报告", MARGIN + 7, 28);
  setFontStyle('normal');
  doc.setTextColor(...COLORS.TEXT_LIGHT);
  doc.setFontSize(10);
  doc.text("AI Smart Selection Report", MARGIN + 7, 36);

  const infoCardY = bannerHeight + 10;
  const cardPadding = 10;
  const colGap = 10;
  const colWidth = (CONTENT_WIDTH - cardPadding * 2 - colGap) / 2;
  const leftX = MARGIN + cardPadding;
  const rightX = leftX + colWidth + colGap;
  const rowFontSize = 11;
  const rowLineHeight = 6.2;

  const majorText = Array.isArray(studentInfo.major) ? studentInfo.major.join('/') : (studentInfo.major || '-');
  const rows = [
    [`学生姓名：${studentInfo.name || '-'}`, `平均成绩：${studentInfo.gpa || '-'}`],
    [`本科院校：${studentInfo.undergraduateSchool || '-'}`, `目标专业：${majorText}`],
    [`生成时间：${new Date().toLocaleDateString()}`, '']
  ];

  doc.setFontSize(rowFontSize);
  setFontStyle('normal');
  doc.setTextColor(...COLORS.TEXT_MAIN);

  const measured = rows.map(([leftText, rightText]) => {
    const leftLines = doc.splitTextToSize(leftText, colWidth);
    const rightLines = rightText ? doc.splitTextToSize(rightText, colWidth) : [];
    const lines = Math.max(leftLines.length, rightLines.length, 1);
    const height = lines * rowLineHeight;
    return { leftLines, rightLines, height };
  });
  const infoCardHeight = cardPadding * 2 + measured.reduce((sum, r) => sum + r.height, 0);

  doc.setFillColor(...COLORS.WHITE);
  doc.setDrawColor(...COLORS.BORDER_STRONG);
  doc.setLineWidth(0.35);
  doc.roundedRect(MARGIN, infoCardY, CONTENT_WIDTH, infoCardHeight, 8, 8, 'FD');

  let rowY = infoCardY + cardPadding + rowFontSize * 0.3527;
  measured.forEach((row) => {
    doc.text(row.leftLines, leftX, rowY);
    if (row.rightLines.length > 0) {
      doc.text(row.rightLines, rightX, rowY);
    }
    rowY += row.height;
  });

  // --- 3. 添加方案汇总表格 ---
  cursor.y = infoCardY + infoCardHeight + 14;
  drawSectionHeader("申请方案汇总", "/ Application Summary");

  // 绘制汇总表
  cursor.y = drawSummaryTable(matchedSchools, cursor.y);

  // --- 4. 循环生成学校详情页 ---
  matchedSchools.forEach((school, index) => {
    doc.addPage();
    cursor.y = MARGIN;

    // 页眉
    doc.setFontSize(9);
    doc.setTextColor(...COLORS.TEXT_LIGHT);
    doc.text(`方案 ${index + 1} / ${matchedSchools.length}`, PAGE_WIDTH - MARGIN - 32, 16);

    // A. 院校 Header（更紧凑卡片）
    const headerHeight = 30;
    const headerTop = cursor.y + 8;
    const headerLeftX = MARGIN + 12;
    const headerContentWidth = CONTENT_WIDTH - 12;
    let headerBottomY = headerTop + 14;
    // 学校名称
    doc.setFontSize(16);
    doc.setTextColor(...COLORS.PRIMARY_DARK);
    setFontStyle('bold');
    doc.text(school.name || '未命名院校', headerLeftX, headerTop + 14);

    // 英文名
    doc.setFontSize(9);
    doc.setTextColor(...COLORS.TEXT_LIGHT);
    setFontStyle('normal');
    if (school.englishName) {
      doc.text(school.englishName, headerLeftX, headerTop + 21);
      headerBottomY = Math.max(headerBottomY, headerTop + 21);
    }

    // QS 排名 (右侧胶囊，更小更精致)
    if (school.rank && school.rank !== 'N/A') {
      const rankText = `QS Ranking · ${school.rank}`;
      doc.setFontSize(8.5);
      const badgeW = doc.getTextWidth(rankText) + 14;
      const badgeX = PAGE_WIDTH - MARGIN - badgeW;
      const badgeY = headerTop + 10;

      doc.setFillColor(...COLORS.ACCENT);
      doc.roundedRect(badgeX, badgeY, badgeW, 10, 5, 5, 'F');

      doc.setTextColor(...COLORS.HEADER_TEXT);
      doc.text(rankText, badgeX + badgeW / 2, badgeY + 5.5, { align: 'center' });
      headerBottomY = Math.max(headerBottomY, badgeY + 10);
    }
    cursor.y = Math.max(headerTop + headerHeight, headerBottomY) + 8;

    // B. 专业信息 & AI 匹配度（去掉色条，用文字描述）
    doc.setFontSize(14);
    doc.setTextColor(...COLORS.TEXT_MAIN);
    const displayMajor = school.major || school.recommendedMajor || '未指定专业';
    const majorTitle = doc.splitTextToSize(`目标专业：${displayMajor}`, headerContentWidth);
    setFontStyle('bold');
    doc.text(majorTitle, headerLeftX, cursor.y);
    setFontStyle('normal');
    cursor.y += majorTitle.length * 6.5 + 4;

    // 匹配度文字描述
    let score = parseFloat(school.matchingScore);
    if (!Number.isFinite(score)) score = 0;
    if (score > 0 && score <= 1) score = score * 100;
    const matchLevel = school.matchLevel || '评估中';
    let matchDesc = '适配度评估中';
    if (Number.isFinite(score)) {
      if (score >= 85) matchDesc = '高度匹配，冲刺+保底均可重点考虑';
      else if (score >= 70) matchDesc = '匹配度较高，建议重点关注';
      else if (score >= 50) matchDesc = '匹配度一般，可做备选方案';
      else matchDesc = '匹配度较低，建议谨慎选择';
    }

    const matchText = `AI 匹配度：${score ? score.toFixed(1) : 0}%（${matchLevel}）- ${matchDesc}`;
    doc.setFontSize(10);
    doc.setTextColor(...COLORS.TEXT_MAIN);
    const matchLines = doc.splitTextToSize(matchText, headerContentWidth);
    doc.text(matchLines, headerLeftX, cursor.y);
    cursor.y += matchLines.length * 5.2 + 6;

    // C. 关键数据
    drawSectionHeader("关键数据", "/ Key Statistics");
    const stats = [
      { label: "学制 (Duration)", value: school.duration || '1年' },
      { label: "授课语言 (Language)", value: school.language || 'English' },
      { label: "开学时间 (Start)", value: school.startDate || 'Sep 2026' },
      { label: "学费 (Tuition)", value: school.tuition || '待查询' },
      { label: "奖学金 (Scholarship)", value: school.scholarshipAvailable ? '有 / Available' : '无 / None' },
      { label: "申请截止 (Deadline)", value: school.applicationDeadline || 'Rolling' }
    ];
    cursor.y = drawKeyStats(stats);

    if (checkPageBreak(26)) cursor.y += 0;

    // D. 专业介绍 (优先使用数据库 description)
    drawSectionHeader("专业介绍", "/ Description");
    const descriptionText = school.description || "暂无官方专业描述数据。";

    // 将专业介绍封装为表格行数据
    const descData = [
      { label: "", value: descriptionText }
    ];
    cursor.y = drawCommonTable(descData, cursor.y);

    // === E. 课程设置 (使用数据库 curriculum + 核心课程表) ===
    if (school.curriculum || (school.coreCourses && school.coreCourses.length > 0)) {

      // 1. 标题
      drawSectionHeader("课程设置", "/ Curriculum");

      // 2. 概述 (如有)
      if (school.curriculum) {
        cursor.y = drawCommonTable([{ label: "", value: school.curriculum }], cursor.y);
      }

      // 3. 核心课程表格
      if (school.coreCourses && school.coreCourses.length > 0) {
        doc.setFontSize(10);
        doc.setTextColor(...COLORS.PRIMARY_DARK);
        setFontStyle('bold');
        doc.text("核心课程 / Core Modules", MARGIN, cursor.y);
        cursor.y += 6;

        cursor.y = drawCoreCoursesTable(school.coreCourses, cursor.y);
      }
    }

    // F. 录取要求 + AI 分析（使用高级表格）
    drawSectionHeader("录取要求", "/ Requirements");

    const academicReq = school.admissionRequirements || "GPA 80%+, 相关专业背景";
    const ieltsReq = school.ieltsRequirement || '—';
    const toeflReq = school.toeflRequirement || '—';
    const greReq = school.greRequirement || '—';
    const gmatReq = school.gmatRequirement || '—';
    const otherReq =
      school.additionalRequirement ||
      school.additionalTestRequirement ||
      school.entryRequirement ||
      '—';

    const requirementGroups = [
      [
        { label: "入学要求", value: academicReq },
        { label: "雅思", value: ieltsReq },
        { label: "托福", value: toeflReq }
      ],
      [
        { label: "GRE", value: greReq },
        { label: "GMAT", value: gmatReq },
        { label: "其他", value: otherReq }
      ]
    ];
    cursor.y = drawApplyGridTable(requirementGroups, cursor.y, 3);

    const aiAnalysis = school.analysis || school.matchReason || '符合申请资格';
    cursor.y = drawApplyGridTable([[{ label: "AI 分析", value: aiAnalysis }]], cursor.y, 1);

    // G. 官网链接
    if (school.website) {
      if (checkPageBreak(20)) cursor.y += 10;
      doc.setTextColor(...COLORS.PRIMARY_DARK);
      doc.setFontSize(9);
      doc.textWithLink(">> 点击访问该专业官方网站", MARGIN, cursor.y, { url: school.website });
    }
  });

  // 4. 添加页脚页码
  const pageCount = doc.internal.getNumberOfPages();
  for (let i = 1; i <= pageCount; i++) {
    doc.setPage(i);
    doc.setFontSize(9);
    doc.setTextColor(150, 150, 150);
    doc.text(`Page ${i} of ${pageCount}`, PAGE_WIDTH / 2, PAGE_HEIGHT - 10, { align: 'center' });
  }

  // 5. 保存文件
  doc.save(`${studentInfo.name || 'Student'}_智能选校报告_${new Date().toISOString().slice(0, 10)}.pdf`);
};
