package com.sparta.foodorder.global.config;

import java.util.concurrent.Executor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
@EnableAsync
public class AsyncConfig {

	@Bean(name = "eventExecutor")
	public Executor eventExecutor() {
		ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor(); // 비동기 작업을 처리할 스레드들을 관리 (스레드 풀 관리하는 Executor 생성)
		executor.setCorePoolSize(2); // 기본 스레드 수 (작업이 없어도 이 2개는 유지)
		executor.setMaxPoolSize(5); // 최대 스레드 수 5 (작업 많으면 최대 5개의 스레드 생성 / 큐가 가득 차면 추가 스레드 생성)
		executor.setQueueCapacity(100); // 대기 큐 크기: 100개 -> 코어 스레드가 다 바쁘면 작업을 큐에 넣고, 큐도 가득 차면 MaxPoolSize까지 스레드 추가 생성
		executor.setThreadNamePrefix("event-async-"); // 로그에 표시될 스레드 이름 접두사 (디버깅 용이)
		executor.setWaitForTasksToCompleteOnShutdown(true); // 애플리케이션 종료 시 대기 중인 작업 완료하고 종료 (true)
		executor.setAwaitTerminationSeconds(60); // 종료 대기 시간 최대 60초 (60초 안에 작업 안끝나면 강제 종료)
		executor.initialize(); // 설정 완료하고 초기화 -> Bean으로 반환
		return executor;
	}
}
