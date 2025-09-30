package com.sparta.foodorder.global.config;

import java.util.Optional;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@Configuration
@EnableJpaAuditing(auditorAwareRef = "auditorProvider")
public class JpaAuditingConfig {

	/**
	 * 현재 로그인한 사용자의 username을 반환하는 AuditorAware 구현
	 */
	@Bean
	public AuditorAware<String> auditorProvider() {
		return new AuditorAwareImpl();
	}

	/**
	 * AuditorAware 구현 클래스
	 * - 로그인된 사용자: username 반환
	 * - 로그인 안 된 경우: "SYSTEM" 반환
	 */
	static class AuditorAwareImpl implements AuditorAware<String> {

		@Override
		public Optional<String> getCurrentAuditor() {
			Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

			// 인증 정보가 없거나 인증되지 않은 경우
			if (authentication == null || !authentication.isAuthenticated()) {
				return Optional.of("SYSTEM");
			}

			// 익명 사용자인 경우
			if ("anonymousUser".equals(authentication.getPrincipal())) {
				return Optional.of("SYSTEM");
			}

			// 로그인된 사용자의 username 반환
			return Optional.of(authentication.getName());
		}
	}
}
