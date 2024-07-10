package fr.eni.tp.encheres.configuration.internationalization;

import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;

@Configuration
public class MessageConfig implements WebMvcConfigurer{
	private static final Logger messageConfigLogger = LoggerFactory.getLogger(MessageConfig.class);
	
	@Bean("messageSource")
	MessageSource messageSource() {
		messageConfigLogger.info("Méthode messageSource");
		ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
	    messageSource.setBasenames("language/messages");
	    messageSource.setDefaultEncoding("UTF-8");
	    return messageSource;
	}
	
	@Bean
	LocaleResolver localeResolver() {
		messageConfigLogger.info("Méthode localResolver");
	    SessionLocaleResolver slr = new SessionLocaleResolver();
	    slr.setDefaultLocale(Locale.FRENCH);
	    slr.setLocaleAttributeName("current.locale");
	    slr.setTimeZoneAttributeName("current.timezone");
	    return slr;
	}
	
	@Bean
	LocaleChangeInterceptor localeChangeInterceptor() {
		messageConfigLogger.info("Méthode localeChangeInterceptor");
	    LocaleChangeInterceptor localeChangeInterceptor = new LocaleChangeInterceptor();
	    localeChangeInterceptor.setParamName("language");
	    return localeChangeInterceptor;
	}
	
	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		messageConfigLogger.info("Méthode addInterceptors");
	    registry.addInterceptor(localeChangeInterceptor());
	}

	  
}
