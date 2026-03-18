package com.example.j2n.auth_srv.utils;

import com.example.j2n.auth_srv.utils.SearchPredicateBuilder.SearchOperation;
import com.example.j2n.dto.PagingRequest;
import com.example.j2n.utils.PageUtil;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.function.Function;

@Slf4j
@Component
@RequiredArgsConstructor
public class SearchFactory {

    /**
     * Builds a JPA Specification based on a list of search criteria.
     * Each criterion is converted to a Predicate and combined with AND.
     */
    public <T> Specification<T> buildSpecification(List<SearchPredicateBuilder.SearchCriteria> searchCriteriaList) {
        return (root, query, criteriaBuilder) -> {
            log.info("Start to build search specification");
            if (CollectionUtils.isEmpty(searchCriteriaList)) {
                log.info("Search criteria list is empty");
                return criteriaBuilder.conjunction();
            }

            Predicate predicates = criteriaBuilder.conjunction();
            for (SearchPredicateBuilder.SearchCriteria searchCriteria : searchCriteriaList) {
                if (!StringUtils.hasText(searchCriteria.getFieldName())
                        || searchCriteria.getOptionalValue().isEmpty()) {
                    log.debug("Ignore search criteria field name [{}] or empty value", searchCriteria.getFieldName());
                    continue;
                }
                Object value = searchCriteria.getOptionalValue().get();
                SearchOperation operation = searchCriteria.getOperation();
                Predicate predicate = SearchPredicateBuilder.buildPredicate(root, criteriaBuilder,
                        searchCriteria.getFieldName(), value, operation);

                predicates = criteriaBuilder.and(predicates, predicate);

                log.debug("Added criteria for field: {} with operation: {}", searchCriteria.getFieldName(), operation);
            }
            return predicates;
        };
    }

    /**
     * Executes a search with criteria and paging.
     * Maps results from entity to DTO using the provided mapper function.
     */
    public <E, R> Page<R> searchAndMap(JpaSpecificationExecutor<E> repository,
            List<SearchPredicateBuilder.SearchCriteria> predicates,
            PagingRequest pagingRequest,
            Function<E, R> mapper) {
        Page<E> entities = search(repository, predicates, pagingRequest);
        return entities.map(mapper);
    }

    /**
     * Executes a search with criteria and paging.
     * Returns the Page of entities.
     */
    public <E> Page<E> search(JpaSpecificationExecutor<E> repository,
            List<SearchPredicateBuilder.SearchCriteria> predicates,
            PagingRequest pagingRequest) {
        Specification<E> spec = buildSpecification(predicates);
        PageRequest pageRequest = PageUtil.buildPageRequest(
                pagingRequest.getPage(),
                pagingRequest.getSize(),
                pagingRequest.getSortField(),
                pagingRequest.getSortDirection());
        return repository.findAll(spec, pageRequest);
    }
}
