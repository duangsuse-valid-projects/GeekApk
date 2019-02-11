import org.duangsuse.geekapk.GeekApkApplicationKt
import org.duangsuse.geekapk.helper.ApiDoc

import javax.servlet.*
import javax.servlet.http.*
import java.security.Principal

GeekApkApplicationKt.initializeINIConfig()

HttpServletRequest hsr = (new HttpServletRequest() {
    String getAuthType() {
        return null
    }

    Cookie[] getCookies() {
        return new Cookie[0]
    }

    long getDateHeader(String name) {
        return 0
    }

    String getHeader(String name) {
        return null
    }

    Enumeration<String> getHeaders(String name) {
        return null
    }

    Enumeration<String> getHeaderNames() {
        return null
    }

    int getIntHeader(String name) {
        return 0
    }


    String getMethod() {
        return null
    }


    String getPathInfo() {
        return null
    }


    String getPathTranslated() {
        return null
    }


    String getContextPath() {
        return "/"
    }


    String getQueryString() {
        return null
    }


    String getRemoteUser() {
        return null
    }


    boolean isUserInRole(String role) {
        return false
    }


    Principal getUserPrincipal() {
        return null
    }


    String getRequestedSessionId() {
        return null
    }


    String getRequestURI() {
        return null
    }


    StringBuffer getRequestURL() {
        return null
    }


    String getServletPath() {
        return null
    }


    HttpSession getSession(boolean create) {
        return null
    }


    HttpSession getSession() {
        return null
    }


    String changeSessionId() {
        return null
    }


    boolean isRequestedSessionIdValid() {
        return false
    }


    boolean isRequestedSessionIdFromCookie() {
        return false
    }


    boolean isRequestedSessionIdFromURL() {
        return false
    }


    boolean isRequestedSessionIdFromUrl() {
        return false
    }


    boolean authenticate(HttpServletResponse response) throws IOException, ServletException {
        return false
    }


    void login(String username, String password) throws ServletException {

    }


    void logout() throws ServletException {

    }


    Collection<Part> getParts() throws IOException, ServletException {
        return null
    }


    Part getPart(String name) throws IOException, ServletException {
        return null
    }


    HttpUpgradeHandler upgrade(Class httpUpgradeHandlerClass) throws IOException, ServletException {
        return null
    }


    Object getAttribute(String name) {
        return null
    }


    Enumeration<String> getAttributeNames() {
        return null
    }


    String getCharacterEncoding() {
        return null
    }


    void setCharacterEncoding(String env) throws UnsupportedEncodingException {

    }


    int getContentLength() {
        return 0
    }


    long getContentLengthLong() {
        return 0
    }


    String getContentType() {
        return null
    }


    ServletInputStream getInputStream() throws IOException {
        return null
    }


    String getParameter(String name) {
        return null
    }


    Enumeration<String> getParameterNames() {
        return null
    }


    String[] getParameterValues(String name) {
        return new String[0]
    }


    Map<String, String[]> getParameterMap() {
        return null
    }


    String getProtocol() {
        return null
    }


    String getScheme() {
        return "http"
    }


    String getServerName() {
        return null
    }


    int getServerPort() {
        return 0
    }


    BufferedReader getReader() throws IOException {
        return null
    }


    String getRemoteAddr() {
        return null
    }


    String getRemoteHost() {
        return null
    }


    void setAttribute(String name, Object o) {

    }


    void removeAttribute(String name) {

    }


    Locale getLocale() {
        return null
    }


    Enumeration<Locale> getLocales() {
        return null
    }


    boolean isSecure() {
        return false
    }


    RequestDispatcher getRequestDispatcher(String path) {
        return null
    }


    String getRealPath(String path) {
        return null
    }


    int getRemotePort() {
        return 0
    }


    String getLocalName() {
        return null
    }


    String getLocalAddr() {
        return "geekapk"
    }


    int getLocalPort() {
        return 80
    }


    ServletContext getServletContext() {
        return null
    }


    AsyncContext startAsync() throws IllegalStateException {
        return null
    }


    AsyncContext startAsync(ServletRequest servletRequest, ServletResponse servletResponse) throws IllegalStateException {
        return null
    }


    boolean isAsyncStarted() {
        return false
    }


    boolean isAsyncSupported() {
        return false
    }


    AsyncContext getAsyncContext() {
        return null
    }


    DispatcherType getDispatcherType() {
        return null
    }
}) as HttpServletRequest

map = ApiDoc.root.invoke(hsr)

for (e in map) {
    println("// Category $e.key")
    for (s in e.value) {
        if (s.key.contains('('))
            println("${s.key} = ${s.value.replace('http://geekapk:80', '')}")
    }
}
