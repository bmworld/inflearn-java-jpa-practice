package jpaWithApi.jpashop;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class JpashopApplication {

  public static void main(String[] args) {

    SpringApplication.run(JpashopApplication.class, args);

  }


  /**
   * API > Reponse 에 Entity를 반환하는 무시무시한 방법(-_-;)을 선택한 경우, 아래와 같이 Hibernate5Module을 사용하시라.
   * 작은 Project일 경우가 아니라면..
   * 극 비추 By 김영한 강사님.
   */
//  @Bean
//  Hibernate5Module hibernate5Module(){
//    Hibernate5Module hibernate5Module = new Hibernate5Module();
//    hibernate5Module.configure(Hibernate5Module.Feature.FORCE_LAZY_LOADING, true);
//    return hibernate5Module;
//  }
}
