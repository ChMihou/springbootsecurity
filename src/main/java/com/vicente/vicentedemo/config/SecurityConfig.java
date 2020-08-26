package com.vicente.vicentedemo.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;

import javax.sql.DataSource;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private AuthenticationProvider authenticationProvider;

    @Autowired
    private AuthenticationSuccessHandler myAuthenticationSucessHandler;

    @Autowired
    private AuthenticationFailureHandler myAuthenticationFailureHandler;

    @Autowired
    private ValidateCodeFilter validateCodeFilter;

    @Autowired
    private DataSource dataSource;

    @Autowired
    private UserDetailsService myUserDetailsService;

    @Autowired
    private LogoutSuccessHandler myLogoutSuccessHandler;

    @Autowired
    private AccessDeniedHandler myAuthenticationAccessDeniedHandler;


    @Override
    public void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth
                //用户认证处理
                .authenticationProvider(authenticationProvider);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                //关闭csrf功能:跨站请求伪造,默认只能通过post方式提交logout请求
                //如果未关闭，会出现注销页面跳转404
                .csrf().disable()
                .headers().frameOptions().disable()
                .and();
        http
                .exceptionHandling()
                .accessDeniedHandler(myAuthenticationAccessDeniedHandler)
                .and();
        http
                //登录处理
                .formLogin()
                .loginPage("/loginPage")
                //数据绑定
                .usernameParameter("username")
                .passwordParameter("password")
                .loginProcessingUrl("/form")
                .successHandler(myAuthenticationSucessHandler)
                .failureHandler(myAuthenticationFailureHandler)
                .permitAll()
                .and();
        http
                //登出处理
                .logout()
                .logoutUrl("/logout")
                .logoutSuccessHandler(myLogoutSuccessHandler)
                .deleteCookies("JSESSIONID")
                .logoutSuccessUrl("/logout")
                //.permitAll()
                .and();
        http
                .addFilterBefore(validateCodeFilter, UsernamePasswordAuthenticationFilter.class)
                .authorizeRequests()
                //无需权限访问
                .antMatchers("/css/**","/download/**","/session/invalid", "/code/*","/error/*","/reportSale/*").permitAll()
                .antMatchers("/user/**").hasRole("USER")
                //其他接口需要登录后才能访问
                .anyRequest().authenticated()
                .and();
        http
                .sessionManagement() // 添加 Session管理器
                .invalidSessionUrl("/session/invalid") // Session失效后跳转到这个链接
                .maximumSessions(1)
                //.expiredSessionStrategy(sessionExpiredStrategy)
                .and();
        http
                //开启记住我
                .rememberMe()
                .rememberMeParameter("remember-me")
                .tokenValiditySeconds(7200) // remember 过期时间，单为秒
                .tokenRepository(persistentTokenRepository())
                .userDetailsService(myUserDetailsService)
                .and();
    }


    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public PersistentTokenRepository persistentTokenRepository() {
        JdbcTokenRepositoryImpl jdbcTokenRepository = new JdbcTokenRepositoryImpl();
        jdbcTokenRepository.setDataSource(dataSource);
        jdbcTokenRepository.setCreateTableOnStartup(false);
        return jdbcTokenRepository;
    }

}