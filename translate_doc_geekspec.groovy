import org.duangsuse.geekapk.GeekApkApplicationKt
import javax.servlet.*
import javax.servlet.http.*
import java.security.*

GeekApkApplicationKt.initializeINIConfig()

HttpServletRequest hsr = (new HttpServletRequest() {
    @Override
    String getAuthType() {
        return null
    }

    @Override
    Cookie[] getCookies() {
        return new Cookie[0]
    }

    @Override
    long getDateHeader(String name) {
        return 0
    }

    @Override
    String getHeader(String name) {
        return null
    }

    @Override
    Enumeration<String> getHeaders(String name) {
        return null
    }

    @Override
    Enumeration<String> getHeaderNames() {
        return null
    }

    @Override
    int getIntHeader(String name) {
        return 0
    }

    @Override
    String getMethod() {
        return null
    }

    @Override
    String getPathInfo() {
        return null
    }

    @Override
    String getPathTranslated() {
        return null
    }

    @Override
    String getContextPath() {
        return "/"
    }

    @Override
    String getQueryString() {
        return null
    }

    @Override
    String getRemoteUser() {
        return null
    }

    @Override
    boolean isUserInRole(String role) {
        return false
    }

    @Override
    Principal getUserPrincipal() {
        return null
    }

    @Override
    String getRequestedSessionId() {
        return null
    }

    @Override
    String getRequestURI() {
        return null
    }

    @Override
    StringBuffer getRequestURL() {
        return null
    }

    @Override
    String getServletPath() {
        return null
    }

    @Override
    HttpSession getSession(boolean create) {
        return null
    }

    @Override
    HttpSession getSession() {
        return null
    }

    @Override
    String changeSessionId() {
        return null
    }

    @Override
    boolean isRequestedSessionIdValid() {
        return false
    }

    @Override
    boolean isRequestedSessionIdFromCookie() {
        return false
    }

    @Override
    boolean isRequestedSessionIdFromURL() {
        return false
    }

    @Override
    boolean isRequestedSessionIdFromUrl() {
        return false
    }

    @Override
    boolean authenticate(HttpServletResponse response) throws IOException, ServletException {
        return false
    }

    @Override
    void login(String username, String password) throws ServletException {

    }

    @Override
    void logout() throws ServletException {

    }

    @Override
    Collection<Part> getParts() throws IOException, ServletException {
        return null
    }

    @Override
    Part getPart(String name) throws IOException, ServletException {
        return null
    }

    @Override
    def HttpUpgradeHandler upgrade(Class httpUpgradeHandlerClass) throws IOException, ServletException {
        return null
    }

    @Override
    Object getAttribute(String name) {
        return null
    }

    @Override
    Enumeration<String> getAttributeNames() {
        return null
    }

    @Override
    String getCharacterEncoding() {
        return null
    }

    @Override
    void setCharacterEncoding(String env) throws UnsupportedEncodingException {

    }

    @Override
    int getContentLength() {
        return 0
    }

    @Override
    long getContentLengthLong() {
        return 0
    }

    @Override
    String getContentType() {
        return null
    }

    @Override
    ServletInputStream getInputStream() throws IOException {
        return null
    }

    @Override
    String getParameter(String name) {
        return null
    }

    @Override
    Enumeration<String> getParameterNames() {
        return null
    }

    @Override
    String[] getParameterValues(String name) {
        return new String[0]
    }

    @Override
    Map<String, String[]> getParameterMap() {
        return null
    }

    @Override
    String getProtocol() {
        return null
    }

    @Override
    String getScheme() {
        return "http"
    }

    @Override
    String getServerName() {
        return null
    }

    @Override
    int getServerPort() {
        return 0
    }

    @Override
    BufferedReader getReader() throws IOException {
        return null
    }

    @Override
    String getRemoteAddr() {
        return null
    }

    @Override
    String getRemoteHost() {
        return null
    }

    @Override
    void setAttribute(String name, Object o) {

    }

    @Override
    void removeAttribute(String name) {

    }

    @Override
    Locale getLocale() {
        return null
    }

    @Override
    Enumeration<Locale> getLocales() {
        return null
    }

    @Override
    boolean isSecure() {
        return false
    }

    @Override
    RequestDispatcher getRequestDispatcher(String path) {
        return null
    }

    @Override
    String getRealPath(String path) {
        return null
    }

    @Override
    int getRemotePort() {
        return 0
    }

    @Override
    String getLocalName() {
        return null
    }

    @Override
    String getLocalAddr() {
        return "geekapk"
    }

    @Override
    int getLocalPort() {
        return 80
    }

    @Override
    ServletContext getServletContext() {
        return null
    }

    @Override
    AsyncContext startAsync() throws IllegalStateException {
        return null
    }

    @Override
    AsyncContext startAsync(ServletRequest servletRequest, ServletResponse servletResponse) throws IllegalStateException {
        return null
    }

    @Override
    boolean isAsyncStarted() {
        return false
    }

    @Override
    boolean isAsyncSupported() {
        return false
    }

    @Override
    AsyncContext getAsyncContext() {
        return null
    }

    @Override
    DispatcherType getDispatcherType() {
        return null
    }
})

import org.duangsuse.geekapk.helper.ApiDoc

map = ApiDoc.root.invoke(hsr)

for (e in map) {
    println("// Category $e.key")
    for (s in e.value) {
        if (s.key.contains('('))
            println("${s.key} = ${s.value.replace('http://geekapk:80', '')}")
    }
}