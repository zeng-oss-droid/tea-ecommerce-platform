# Banner图片存放目录

## 使用说明

1. 将banner图片文件（jpg、png等格式）放在此目录下
2. 在代码中通过 `/banner/图片文件名` 访问图片
3. 例如：如果图片名为 `banner1.jpg`，则访问路径为 `/banner/banner1.jpg`

## 示例

假设你有一个图片 `banner1.jpg`，在代码中可以这样使用：

```vue
<img src="/banner/banner1.jpg" alt="Banner" />
```

或者：

```javascript
const bannerImage = '/banner/banner1.jpg'
```

## 注意事项

- 图片文件名建议使用英文和数字，避免使用中文和特殊字符
- 建议图片尺寸：1200x450 像素
- 图片格式支持：jpg、png、webp等

