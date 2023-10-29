//import jakarta.servlet.*;
//import jakarta.servlet.annotation.WebFilter;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import jakarta.servlet.http.HttpSession;
//import java.io.IOException;
//
//@WebFilter("/*") // Apply the filter to all requests
//public class AuthenticationFilter implements Filter {
//
//    public void init(FilterConfig filterConfig) throws ServletException {
//    }
//
//    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
//            throws IOException, ServletException {
//        HttpServletRequest httpRequest = (HttpServletRequest) request;
//        HttpServletResponse httpResponse = (HttpServletResponse) response;
//
//        HttpSession session = httpRequest.getSession();
//        String username = (String) session.getAttribute("email");
//
////        if (username != null) {
////            // User is logged in, allow access to the requested resource
////            httpResponse.sendRedirect("movie-list.html");
////        } else {
////            // User is not logged in, redirect to the login page
////            httpResponse.sendRedirect("movie-list.html");
////        }
//    }
//
//    public void destroy() {
//    }
//}
