package com.a7a7.common.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.a7a7.common.interceptor.CheckLoginSessionInterceptor;

@Configuration
public class WebMvcConfigurerImpl implements WebMvcConfigurer{

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(new CheckLoginSessionInterceptor())
//			.order(1)
			.addPathPatterns("/*/*/*Xdm*", "/*/*/*Usr*")
			.excludePathPatterns(
//					"/resources/**",
					"/user/**",
					"/usr/signin/signinUsr",
					"/usr/userUi/MemberUsrForm",
					"/usr/signin/signinUsrProc",
					"/usr/userUi/checkId",
					"/usr/userUi/MemberUsrInst",
					"/usr/index/Index",
					"/usr/product/ProductUsrList",
					"/usr/product/ProductUsrView",
//					"/v1/infra/member/sendMailGoogleCertificationUsrProc",
//					"/v1/infra/member/certificationCheckUsrProc",
//					"/v1/infra/member/memberUsrInst",
//					"/v1/infra/member/findIdUsrForm",
//					"/v1/infra/member/findPwdUsrForm",
//					"/v1/infra/index/indexUsrView",
//					"/v1/infra/member/findIdUsrProc",
//					"/v1/infra/member/modalTermsUsrView",
//					"/v1/infra/member/changPwdUsrProc",
//					"/v1/infra/member/withdrawUsrProc",
//					"/v1/mallbicycle/major/majorShowUsrAjaxList",
//					"/v1/mallbicycle/major/majorShowUsrLita",
//					"/v1/mallbicycle/major/majorShowUsrView",
//					"/adt/**",
					"/assets/**",
//					"/xdm/infra/member/signupXdmForm",
					"/xdm/signin/signinXdm",
					"/xdm/signin/signinXdmProc",
					"/xdm/signin/signoutXdmProc"
		);
	}

}

