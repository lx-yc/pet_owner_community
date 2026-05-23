/**
 * 图片压缩工具
 * 支持手机端和电脑端自动适配压缩参数
 */

const COMPRESS_CONFIG = {
  mobile: {
    maxWidth: 1200,
    maxHeight: 1200,
    quality: 0.6,
    maxSize: 2 * 1024 * 1024, // 2MB
  },
  desktop: {
    maxWidth: 1920,
    maxHeight: 1920,
    quality: 0.8,
    maxSize: 5 * 1024 * 1024, // 5MB
  },
}

function isMobile() {
  return /Android|webOS|iPhone|iPad|iPod|BlackBerry|IEMobile|Opera Mini/i.test(navigator.userAgent) ||
    (window.innerWidth <= 768)
}

function compressImage(file, config) {
  return new Promise((resolve, reject) => {
    if (!file.type.startsWith('image/')) {
      resolve(file)
      return
    }

    if (file.type === 'image/gif') {
      resolve(file)
      return
    }

    if (file.size <= config.maxSize) {
      resolve(file)
      return
    }

    const reader = new FileReader()
    reader.onload = (e) => {
      const img = new Image()
      img.onload = () => {
        const canvas = document.createElement('canvas')
        let width = img.width
        let height = img.height

        if (width > config.maxWidth || height > config.maxHeight) {
          const ratio = Math.min(
            config.maxWidth / width,
            config.maxHeight / height
          )
          width = Math.floor(width * ratio)
          height = Math.floor(height * ratio)
        }

        canvas.width = width
        canvas.height = height

        const ctx = canvas.getContext('2d')
        ctx.drawImage(img, 0, 0, width, height)

        canvas.toBlob(
          (blob) => {
            if (blob) {
              const compressedFile = new File([blob], file.name, {
                type: 'image/jpeg',
                lastModified: Date.now(),
              })
              resolve(compressedFile)
            } else {
              reject(new Error('图片压缩失败'))
            }
          },
          'image/jpeg',
          config.quality
        )
      }
      img.onerror = () => reject(new Error('图片加载失败'))
      img.src = e.target.result
    }
    reader.onerror = () => reject(new Error('文件读取失败'))
    reader.readAsDataURL(file)
  })
}

export async function compressImageForUpload(file) {
  const config = isMobile() ? COMPRESS_CONFIG.mobile : COMPRESS_CONFIG.desktop
  return compressImage(file, config)
}

export function getImageSizeInfo(file) {
  return new Promise((resolve, reject) => {
    const reader = new FileReader()
    reader.onload = (e) => {
      const img = new Image()
      img.onload = () => {
        resolve({
          width: img.width,
          height: img.height,
          size: file.size,
          type: file.type,
        })
      }
      img.onerror = () => reject(new Error('图片加载失败'))
      img.src = e.target.result
    }
    reader.onerror = () => reject(new Error('文件读取失败'))
    reader.readAsDataURL(file)
  })
}
