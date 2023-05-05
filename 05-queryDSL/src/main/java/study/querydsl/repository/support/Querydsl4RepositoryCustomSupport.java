package study.querydsl.repository.support;

import com.querydsl.core.types.EntityPath;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.JpaEntityInformationSupport;
import org.springframework.data.jpa.repository.support.Querydsl;
import org.springframework.data.querydsl.SimpleEntityPathResolver;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import java.util.List;
import java.util.function.Function;


/**
 * Querydsl 4.x Ver. 전용 Querydsl 지원 Custom Library
 */
@Repository
public abstract class Querydsl4RepositoryCustomSupport {
  
  private final Class domainClass;
  private Querydsl querydsl;
  private EntityManager entityManager;
  private JPAQueryFactory queryFactory;
  
  public Querydsl4RepositoryCustomSupport(Class domainClass) {
    Assert.notNull(domainClass, "DomainClass must no be null!");
    this.domainClass = domainClass;
  }
  
  @Autowired
  public void setEntityManager(EntityManager em) {
    Assert.notNull(em, "EntityManager must not be null!");
    
    JpaEntityInformation entityInformation = JpaEntityInformationSupport.getEntityInformation(domainClass, em);
    SimpleEntityPathResolver resolver = SimpleEntityPathResolver.INSTANCE;
    EntityPath path = resolver.createPath(entityInformation.getJavaType());
    this.entityManager = em;
    this.querydsl = new Querydsl(em, new PathBuilder<>(path.getType(), path.getMetadata()));
    this.queryFactory = new JPAQueryFactory(em);
  }
  
  
  @PostConstruct
  public void validate() {
    Assert.notNull(entityManager, "EntityManger must not be null!");
    Assert.notNull(querydsl, "Querydsl must not be null!");
    Assert.notNull(queryFactory, "QueryFactory must not be null!");
  }
  
  protected JPAQueryFactory getQueryFactory() {
    return queryFactory;
  }
  
  protected Querydsl getQuerydsl() {
    return querydsl;
  }
  
  protected EntityManager getEntityManager() {
    return entityManager;
  }
  
  protected <T> JPAQuery<T> select(Expression<T> expr) {
    return getQueryFactory().select(expr);
  }
  
  protected <T> JPAQuery<T> selectFrom(EntityPath<T> from) {
    return getQueryFactory().selectFrom(from);
  }
  
  
  protected <T> Page<T> applyPagination (Pageable pageable, Function<JPAQueryFactory, JPAQuery> contentQuery) {
    JPAQuery jpaQuery = contentQuery.apply(getQueryFactory());
    List<T> content = getQuerydsl().applyPagination(pageable, jpaQuery).fetch();
    return PageableExecutionUtils.getPage(content, pageable, jpaQuery::fetchCount);
  }
  
  
  protected <T> Page<T> applyPagination (Pageable pageable, Function<JPAQueryFactory, JPAQuery> contentQuery, Function<JPAQueryFactory, JPAQuery> countQuery) {
    JPAQuery jpaContentQuery = contentQuery.apply(getQueryFactory());
    List<T> content = getQuerydsl().applyPagination(pageable, jpaContentQuery).fetch();
    
    JPAQuery countResult = countQuery.apply(getQueryFactory());
    return PageableExecutionUtils.getPage(content, pageable, countResult::fetchCount);
  }
  
  
}
