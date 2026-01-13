const sharp = require('sharp');
const fs = require('fs');
const path = require('path');

// 需要优化的图片列表
const imagesToOptimize = [
  'hero-bg.jpg',
  'self-apply.jpg', 
  'rebate.jpg',
  'popular-study.jpg',
  'customer-service.jpg'
];

const inputDir = path.join(__dirname, '../public/images');
const outputDir = path.join(__dirname, '../public/images/optimized');

// 创建输出目录
if (!fs.existsSync(outputDir)) {
  fs.mkdirSync(outputDir, { recursive: true });
}

async function optimizeImage(filename) {
  const inputPath = path.join(inputDir, filename);
  const outputPath = path.join(outputDir, filename);
  const webpPath = path.join(outputDir, filename.replace(/\.(jpg|jpeg|png)$/i, '.webp'));
  
  try {
    // 检查文件是否存在
    if (!fs.existsSync(inputPath)) {
      console.log(`文件不存在: ${filename}`);
      return;
    }
    
    // 获取原始文件大小
    const originalStats = fs.statSync(inputPath);
    const originalSize = (originalStats.size / 1024 / 1024).toFixed(2);
    
    console.log(`正在优化: ${filename} (原始大小: ${originalSize}MB)`);
    
    // 优化JPEG/PNG格式 - 压缩质量80%，最大宽度1920px
    await sharp(inputPath)
      .resize(1920, null, { 
        withoutEnlargement: true,
        fit: 'inside'
      })
      .jpeg({ quality: 80, progressive: true })
      .toFile(outputPath);
    
    // 生成WebP格式 - 压缩质量75%
    await sharp(inputPath)
      .resize(1920, null, { 
        withoutEnlargement: true,
        fit: 'inside'
      })
      .webp({ quality: 75 })
      .toFile(webpPath);
    
    // 获取优化后的文件大小
    const optimizedStats = fs.statSync(outputPath);
    const webpStats = fs.statSync(webpPath);
    const optimizedSize = (optimizedStats.size / 1024 / 1024).toFixed(2);
    const webpSize = (webpStats.size / 1024 / 1024).toFixed(2);
    const savings = ((originalStats.size - optimizedStats.size) / originalStats.size * 100).toFixed(1);
    const webpSavings = ((originalStats.size - webpStats.size) / originalStats.size * 100).toFixed(1);
    
    console.log(`✅ ${filename}:`);
    console.log(`   JPEG: ${originalSize}MB → ${optimizedSize}MB (节省 ${savings}%)`);
    console.log(`   WebP: ${originalSize}MB → ${webpSize}MB (节省 ${webpSavings}%)`);
    
  } catch (error) {
    console.error(`❌ 优化失败 ${filename}:`, error.message);
  }
}

async function optimizeAllImages() {
  console.log('开始优化图片...');
  console.log('='.repeat(50));
  
  for (const filename of imagesToOptimize) {
    await optimizeImage(filename);
    console.log('');
  }
  
  console.log('='.repeat(50));
  console.log('图片优化完成！');
  console.log('\n使用说明:');
  console.log('1. 将 public/images/optimized/ 目录下的优化图片替换原图片');
  console.log('2. 或者修改代码中的图片路径指向 /images/optimized/ 目录');
  console.log('3. WebP格式可以进一步减小文件大小，建议在支持的浏览器中使用');
}

// 检查是否安装了sharp
try {
  require.resolve('sharp');
  optimizeAllImages();
} catch (error) {
  console.error('❌ 请先安装 sharp 依赖:');
  console.error('npm install sharp --save-dev');
  process.exit(1);
}