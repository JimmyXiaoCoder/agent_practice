// 引入 @umijs/openapi 提供的核心工具, 调用了 generateService(...)
import { generateService } from '@umijs/openapi'

generateService({
  // 1. 你的 request 封装路径
  requestLibPath: "import request from '@/utils/request'",

  // 2. 后端接口文档地址 (确保你后端 8123 端口开着)
  schemaPath: 'http://localhost:8123/api/v3/api-docs',

  // 3. 生成代码存放的根目录
  // 建议改回 './src/services' 或者 './src/api'，
  // 否则它会直接把一堆文件夹丢在 src 下，会让项目目录很乱
  serversPath: './src',

  // 4. 项目名称，它会作为文件夹名字生成在 serversPath 下
  projectName: 'api',
})
