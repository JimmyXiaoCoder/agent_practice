declare namespace API {
  type adminGetAppByIdParams = {
    id: number
  }

  type App = {
    id?: number
    appName?: string
    cover?: string
    initPrompt?: string
    codeGenType?: string
    deployKey?: string
    deployedTime?: string
    priority?: number
    userId?: number
    editTime?: string
    createTime?: string
    updateTime?: string
    isDelete?: number
  }

  type AppAddRequest = {
    appName?: string
    initPrompt?: string
    cover?: string
    codeGenType?: string
  }

  type AppAdminUpdateRequest = {
    id?: number
    appName?: string
    cover?: string
    priority?: number
  }

  type AppDeployRequest = {
    id?: number
    deployKey?: string
  }

  type AppQueryRequest = {
    pageNum?: number
    pageSize?: number
    sortField?: string
    sortOrder?: string
    id?: number
    appName?: string
    cover?: string
    initPrompt?: string
    codeGenType?: string
    deployKey?: string
    priority?: number
    userId?: number
  }

  type AppUpdateMyRequest = {
    id?: number
    appName?: string
  }

  type AppVO = {
    id?: number
    appName?: string
    cover?: string
    initPrompt?: string
    codeGenType?: string
    deployKey?: string
    deployedTime?: string
    priority?: number
    userId?: number
    editTime?: string
    createTime?: string
    updateTime?: string
    userVO?: UserVO
  }

  type BaseResponseApp = {
    data?: App
    code?: number
    message?: string
  }

  type BaseResponseAppVO = {
    data?: AppVO
    code?: number
    message?: string
  }

  type BaseResponseBoolean = {
    data?: boolean
    code?: number
    message?: string
  }

  type BaseResponseListUserVO = {
    data?: UserVO[]
    code?: number
    message?: string
  }

  type BaseResponseLong = {
    data?: number
    code?: number
    message?: string
  }

  type BaseResponsePageAppVO = {
    data?: PageAppVO
    code?: number
    message?: string
  }

  type BaseResponseString = {
    data?: string
    code?: number
    message?: string
  }

  type BaseResponseUserVO = {
    data?: UserVO
    code?: number
    message?: string
  }

  type chatToGenCodeParams = {
    message: string
    appId: number
  }

  type DeleteRequest = {
    id?: number
  }

  type getInfoParams = {
    id: number
  }

  type getMyAppByIdParams = {
    id: number
  }

  type PageAppVO = {
    records?: AppVO[]
    pageNumber?: number
    pageSize?: number
    totalPage?: number
    totalRow?: number
    optimizeCountQuery?: boolean
  }

  type pageParams = {
    page: PageUser
  }

  type PageUser = {
    records?: User[]
    pageNumber?: number
    pageSize?: number
    totalPage?: number
    totalRow?: number
    optimizeCountQuery?: boolean
  }

  type removeParams = {
    id: number
  }

  type serveFrontendFileParams = {
    deployKey: string
  }

  type serveIndexParams = {
    deployKey: string
  }

  type ServerSentEventString = true

  type User = {
    id?: number
    userAccount?: string
    userPassword?: string
    userName?: string
    userAvatar?: string
    userProfile?: string
    userRole?: string
    editTime?: string
    createTime?: string
    updateTime?: string
    isDelete?: number
  }

  type UserLoginRequest = {
    userAccount?: string
    userPassword?: string
  }

  type UserRegisterRequest = {
    userAccount?: string
    userPassword?: string
    checkPassword?: string
  }

  type UserVO = {
    id?: number
    userAccount?: string
    userName?: string
    userAvatar?: string
    userProfile?: string
    userRole?: string
    createTime?: string
    updateTime?: string
  }
}
