package ru.itsinfo.fetchapi.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.password.PasswordEncoder;
import ru.itsinfo.fetchapi.config.handler.MyAccessDeniedHandler;
import ru.itsinfo.fetchapi.config.handler.AuthenticationFailureHandler;
import ru.itsinfo.fetchapi.config.handler.AuthenticationSuccessHandler;
import ru.itsinfo.fetchapi.config.handler.UrlLogoutSuccessHandler;
import ru.itsinfo.fetchapi.service.AppService;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    // сервис, с помощью которого тащим пользователя
    private final AppService appService;

    private final PasswordEncoder passwordEncoder;

    // класс, в котором описана логика перенаправления пользователей по ролям
    private final AuthenticationSuccessHandler authenticationSuccessHandler;

    // класс, в котором описана логика при неудачной авторизации
    private final AuthenticationFailureHandler authenticationFailureHandler;

    // класс, в котором описана логика при удачной авторизации
    private final UrlLogoutSuccessHandler urlLogoutSuccessHandler;

    // класс, в котором описана логика при отказе в доступе
    private final MyAccessDeniedHandler accessDeniedHandler;

    @Autowired
    public SecurityConfig(AppService appService,
                          PasswordEncoder passwordEncoder,
                          AuthenticationSuccessHandler authenticationSuccessHandler,
                          AuthenticationFailureHandler authenticationFailureHandler,
                          UrlLogoutSuccessHandler urlLogoutSuccessHandler,
                          MyAccessDeniedHandler accessDeniedHandler) {
        this.appService = appService;
        this.passwordEncoder = passwordEncoder;
        this.authenticationSuccessHandler = authenticationSuccessHandler;
        this.authenticationFailureHandler = authenticationFailureHandler;
        this.urlLogoutSuccessHandler = urlLogoutSuccessHandler;
        this.accessDeniedHandler = accessDeniedHandler;
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(appService).passwordEncoder(passwordEncoder);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable()
                // Декларирует, что все запросы к любой конечной точке должны быть авторизованы, иначе они должны быть отклонены
                .authorizeRequests()
                .antMatchers("/", "/img/**", "/css/**", "/js/**", "/webjars/**").permitAll()
//                .antMatchers(HttpMethod.GET, "/api/users/*").hasRole("USER")
                .antMatchers("/api/users/*", "/api/roles").hasRole("ADMIN")
                .anyRequest().authenticated()
                .and()
                .exceptionHandling().accessDeniedHandler(accessDeniedHandler);
//                .and().sessionManagement().disable(); // сообщает Spring, что не следует хранить информацию о сеансе для пользователей, поскольку это не нужно для API
//                .and().httpBasic(); // сообщает Spring, чтобы он ожидал базовую HTTP аутентификацию

        http.formLogin()
                .loginPage("/") // указываем страницу с формой логина
                .permitAll() // даем доступ к форме логина всем
                .loginProcessingUrl("/login")
                .successHandler(authenticationSuccessHandler)
                .failureHandler(authenticationFailureHandler);
        http.logout()
                .logoutUrl("/logout")
                .clearAuthentication(true)
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
                .logoutSuccessUrl("/")
                .logoutSuccessHandler(urlLogoutSuccessHandler)
                .permitAll()
        ;
    }
}