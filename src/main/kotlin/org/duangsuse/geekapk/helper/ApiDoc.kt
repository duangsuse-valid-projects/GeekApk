package org.duangsuse.geekapk.helper

import javax.servlet.http.HttpServletRequest

/**
 * The API Document for GeekApk 1.0
 *
 * @author duangsuse
 * @since 0.1
 * @version 1.0
 */
object ApiDoc {
  private const val description = "description"
  private const val schema = "schema"

  private val newsApp = mustGetGeekApkConfig("newsMetaApp")
  private val badgeApp = mustGetGeekApkConfig("badgeMetaApp")
  private val pictureApp = mustGetGeekApkConfig("pictureMetaApp")

  // TODO Add GetXXXXCount interfaces
  // TODO Update user online status APIs
  // TODO GeekHits and rate limit APIs
  // TODO Query Combination Language APIs
  // TODO GeekPush Feature

  /**
   * Generate something like `request_location/string`
   *
   * @param req Input request
   * @receiver appended hierarchical part
   * @return intern string like request-location/this
   */
  private fun String.href(req: HttpServletRequest) = "${req.scheme}://${req.localAddr}:${req.localPort}/$this".intern()

  /**
   * Base url property document
   *
   * @param url api base path
   * @return "basePath" to url
   */
  private fun baseUrl(url: String): (HttpServletRequest) -> Pair<String, String> = { hsr -> "basePath" to url.href(hsr) }

  val server = fun (hsr: HttpServletRequest) = "server" to mapOf(
    baseUrl("")(hsr),
    description to "GeekApk server information related APIs",
    schema to "ALL INTERFACES REQUIRES NONE",
    "serverSpec" to "JsonRpc",
    "projectIndex" to "https://github.com/duangsuse/GeekApk",
    "apiVersion" to "v1.0",
    "newsMetaAppId" to newsApp,
    "badgeMetaAppId" to badgeApp,
    "pictureMetaAppId" to pictureApp,
    "index() -> object<category,object<operation,linkTemplate>>" to "".href(hsr),
    "version() -> plain" to "serverVersion".href(hsr),
    "desc() -> plain" to "serverDescription".href(hsr),
    "upTime() -> (string|number):datetime" to "serverBoot".href(hsr),
    "detailedInfo() -> object:<prop,desc>" to "serverDetail".href(hsr)
  )

  val admin = fun (hsr: HttpServletRequest) = "admin" to mapOf(
    baseUrl("admin")(hsr),
    description to "GeekApk community administration functions",
    /* `admin flag` in GeekApk JUST a flag, modTok is required for non-deleteApp,transAppCategory,deleteComment operations */
    schema to "ALL INTERFACES BUT (deleteApp,transferAppCategory,deleteComment,flagUser)#a REQUIRES Cookie(gaModTok) BE " +
      "`geekapk admin token` AND (Cookie(gaUser), Cookie(gaHash) BE `valid login`)#b; #a REQUIRES REWRITE #b `valid superuser login`",
    "POST@createUser(username) -> object:user" to "admin/makeUser".href(hsr),
    "PUT@resetSharedHash(uid,shash?) -> plain" to "admin/resetMetaHash/{uid}".href(hsr),
    "DELETE@deleteUser(uid) -> object:user" to "admin/dropUser/{uid}".href(hsr),
    "PUT@flagUser(uid,flag) -> object:user" to "admin/flagUser/{uid}".href(hsr),
    "POST@createCategory(name) -> object:category" to "admin/makeCategory".href(hsr),
    "PUT@renameCategory(id,name) -> object:category" to "admin/nameCategory/{id}".href(hsr),
    "DELETE@deleteCategory(id) -> object:category" to "admin/dropCategory/{id}".href(hsr),
    "DELETE@deleteApp(aid) -> object:app" to "admin/dropApp/{aid}".href(hsr),
    "PUT@transferAppCategory(aid,cid) -> object:[aid,old,new]" to "admin/moveApp/{aid}".href(hsr),
    "PUT@transferAppOwner(aid,uid) -> object:[aid,old,new]" to "admin/transferApp/{aid}".href(hsr),
    "DELETE@deleteAppUpdate(aid,rev) -> object:appUpdate" to "admin/dropAppUpdate/{aid}/{rev}".href(hsr),
    "DELETE@deleteComment(cid) -> object:[comment,deletedSubCommentsCount]" to "admin/dropComment/{cid}".href(hsr)
  )

  val category = fun (hsr: HttpServletRequest) = "category" to mapOf( /* trivially */
    baseUrl("category")(hsr),
    description to "Application Categories",
    schema to "ALL INTERFACES REQUIRES NONE",
    "categoryList() -> array:category" to "category/all".href(hsr),
    "categoryName(id) -> plain" to "category/{id}".href(hsr)
  )

  val user = fun (hsr: HttpServletRequest) = "user" to mapOf(
    baseUrl("user")(hsr),
    description to "GeekApk user APIs",
    schema to "INTERFACE updateUser REQUIRES Cookie(gaHash) BE `valid hash`",
    "readUser(id) -> object:user" to "user/{id}".href(hsr),
    "PUT@updateUser(id,prop{username,nickname,avatar,bio,metaApp},value) -> [user,prop,old,new]" to "user/{id}".href(hsr),
    "PUT@resetHash(id,shash,hash) -> [id,newShash,newHash]" to "user/{id}/hash".href(hsr),
    "checkHash(id,hash) -> [valid,message]" to "user/{id}/checkHash".href(hsr),
    "listUser(sort?{created,followers},sliceFrom?,sliceTo?) -> array:object:user" to "user/all".href(hsr),
    "listMetaUser(sort?{created,followers},sliceFrom?,sliceTo?) -> array:object:user" to "user/allHasMetaApp".href(hsr),
    "searchUser(type?{username,nickname,bio},kw,sort?{created,followers}) -> array:object:user" to "user/search/{kw}".href(hsr)
  )

  val timeline = fun (hsr: HttpServletRequest) = "timeline" to mapOf(
    baseUrl("timeline")(hsr),
    description to "GeekApk user timeline functions",
    schema to "ALL INTERFACES REQUIRES NONE",
    "readUserTimeline(uid,type,sliceFrom?,sliceTo?) -> array:timeline" to "timeline/{uid}".href(hsr),
    "readAllTimeline(type,sliceFrom?,sliceTo?) -> array:timeline" to "timeline/all".href(hsr),
    "bulkReadUserTimeline(uids,type,sliceFrom?,sliceTo?) -> array:timeline" to "timeline/bulk/{uids}".href(hsr)
  )

  val notification = fun (hsr: HttpServletRequest) = "notification" to mapOf(
    baseUrl("notification")(hsr),
    description to "GeekApk user private notification APIs",
    schema to "ALL INTERFACES REQUIRES Cookie(gaHash, gaUser) BE `valid login`",
    "readMineNotifications() -> array:notification" to "notification/active".href(hsr),
    "readAllMineNotifications(sliceFrom?,sliceTo?) -> array:notification" to "notification/all".href(hsr),
    "markNotifications(start,end?,stat{r,u}) -> number" to "notification/mark".href(hsr)
  )

  val app = fun (hsr: HttpServletRequest) = "app" to mapOf(
    baseUrl("app")(hsr),
    description to "GeekApk Android application metadata APIs",
    schema to "ALL NON GET INTERFACE REQUIRES Cookie(gaUser), Cookie(gaHash) BE `valid non-readonly login`",
    "readApp(aid) -> object:app" to "app/{aid}".href(hsr),
    "PUT@updateApp(aid,attr{package,icon,name,screenshots,readme},val) -> [attr,oldVal]" to "app/{aid}".href(hsr),
    "POST@createApp(package,category) -> object:app" to "app".href(hsr),
    "findAppWithPackage(package) -> object:app" to "app/package/{package}".href(hsr),
    "listApp(inCategory?,sort,sliceFrom?,sliceTo?) -> array:app" to "app/all".href(hsr),
    "DELETE@dropApp(aid) -> object:app" to "app/{aid}".href(hsr),
    "searchApp(inCategory?,content,type{name,package,icon,readme},sort{updated,comments,stars,created})" to "app/search/{content}".href(hsr),
    "POST@addCollab(uid,aid)" to "app/{aid}/collab".href(hsr),
    "DELETE@removeCollab(uid,aid)" to "app/{aid}/collab".href(hsr),
    "collaborators(aid) -> array:number" to "app/collaborators/{aid}".href(hsr),
    "collaborated(uid) -> array:number" to "app/collaborated/{uid}".href(hsr)
  )

  val appUpdate = fun (hsr: HttpServletRequest) = "appUpdate" to mapOf(
    baseUrl("appUpdate")(hsr),
    description to "GeekApk Android application reversion metadata APIs",
    schema to "ALL NON GET INTERFACE REQUIRES Cookie(gaUser), Cookie(gaHash) BE `valid non-readonly login`",
    "readReversions(aid) -> array:object:reversion" to "appUpdate/{aid}".href(hsr),
    "checkLastReversions(aids) -> array:number" to "appUpdate/check/{aids}".href(hsr),
    "readReversion(aid,rev) -> object:reversion" to "appUpdate/{aid}/{rev}".href(hsr),
    "POST@createReversion(aid,rev) -> [aid,rev]" to "appUpdate/{aid}/{rev}".href(hsr),
    "PUT@updateReversion(aid,rev,attr{version,install,updates,minsdk},val) -> [attr,oldVal]" to "appUpdate/{aid}/{rev}".href(hsr),
    "DELETE@dropReversion(aid,rev) -> object:reversion" to "appUpdate/{aid}/{rev}".href(hsr)
  )

  val comment = fun (hsr: HttpServletRequest) = "comment" to mapOf(
    baseUrl("comment")(hsr),
    description to "GeekApk User->App comment interfaces",
    schema to "ALL NON GET INTERFACE REQUIRES Cookie(gaUser), Cookie(gaHash) BE `valid non-readonly login`",
    "searchComment(inApp?,user?,repliesTo?,content) -> array:object:comment" to "comment/search/{content}".href(hsr),
    "listCommentInApp(aid,sliceFrom?,sliceTo?) -> array:object:comment" to "comment/{aid}".href(hsr),
    "listSubComment(cid) -> array:object:comment" to "comment/subOf/{cid}".href(hsr),
    "listAllComment(inApp?,user?,sliceFrom?,sliceTo?) -> array:object:comment" to "comment/all".href(hsr),
    "POST@createComment(aid,content) -> object:comment" to "comment/{aid}".href(hsr),
    "PUT@editComment(cid) -> [oldContent,newContent]" to "comment/edit/{cid}".href(hsr),
    "DELETE@deleteComment(cid) -> object:comment" to "comment/delete/{cid}".href(hsr)
  )

  val follow = fun (hsr: HttpServletRequest) = "follow" to mapOf(
    baseUrl("follow")(hsr),
    description to "GeekApk User->User follow relation operations",
    schema to "ALL NON GET INTERFACE REQUIRES Cookie(gaUser), Cookie(gaHash) BE `valid non-banned login`",
    "POST@follow(uid) -> [oldCount,newCount]" to "follow/{uid}".href(hsr),
    "DELETE@unfollow(uid) -> [oldCount,newCount]" to "follow/{uid}".href(hsr),
    "followers(uid) -> array:object:user" to "follow/followers/{uid}".href(hsr),
    "following(uid) -> array:object:user" to "follow/{uid}".href(hsr)
  )

  val star = fun (hsr: HttpServletRequest) = "star" to mapOf(
    baseUrl("star")(hsr),
    description to "GeekApk User->App star functions",
    schema to "ALL NON GET INTERFACE REQUIRES Cookie(gaUser), Cookie(gaHash) BE `valid non-banned login`",
    "POST@star(aid) -> [oldCount,newCount]" to "star/{aid}".href(hsr),
    "DELETE@unStar(aid) -> [oldCount,newCount]" to "star/{aid}".href(hsr),
    "stargazers(aid) -> array:object:user" to "star/{aid}".href(hsr),
    "stars(uid) -> array:object:app" to "star/user/{uid}".href(hsr)
  )

  /**
   * Root API documentation
   */
  val root = fun (hsr: HttpServletRequest) = mapOf(
    server(hsr),
    admin(hsr),

    category(hsr),
    user(hsr),
    timeline(hsr),
    notification(hsr),

    app(hsr),
    appUpdate(hsr),

    comment(hsr),

    follow(hsr),
    star(hsr)
  )
}
