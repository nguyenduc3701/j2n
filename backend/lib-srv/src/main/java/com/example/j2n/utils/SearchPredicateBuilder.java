package com.example.j2n.utils;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.Nullable;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Optional;

import static com.example.j2n.utils.SearchPredicateBuilder.SearchOperation.*;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Slf4j
public class SearchPredicateBuilder {

    public static final String WILD_CARD_ANY_VALUE = "%%%s%%";

    public enum SearchOperation {
        EQUAL,
        NOT_EQUAL,
        LESS_THAN,
        LESS_THAN_EQUAL,
        GREATER_THAN,
        GREATER_THAN_EQUAL,
        DATE_RANGE_OVERLAPS,
        IN,
        LIKE,
        UNKNOWN
    }

    /**
     * Encapsulates date range parameters for overlap checking.
     * Used with DATE_RANGE_OVERLAPS operation.
     */
    @Getter
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class DateRangeValue {
        private String startFieldName = "";
        private String endFieldName = "";
        private @Nullable LocalDateTime start;
        private @Nullable LocalDateTime end;

        public static DateRangeValue of(String startFieldName,
                String endFieldName,
                @Nullable LocalDateTime start,
                @Nullable LocalDateTime end) {
            DateRangeValue value = new DateRangeValue();
            value.startFieldName = startFieldName;
            value.endFieldName = endFieldName;
            value.start = start;
            value.end = end;
            return value;
        }
    }

    /**
     * Builds a JPA Criteria Predicate for the given field, value, and operation.
     */
    public static Predicate buildPredicate(Root<?> root,
            CriteriaBuilder criteriaBuilder,
            String fieldName,
            Object value,
            SearchOperation operation) {
        log.debug("Adding criteria for field: {} with operation: {} and value: {}", fieldName, operation, value);
        return switch (operation) {
            case EQUAL -> buildEqualPredicate(root, criteriaBuilder, fieldName, value);
            case NOT_EQUAL -> buildNotEqualPredicate(root, criteriaBuilder, fieldName, value);
            case LESS_THAN -> buildLessThanPredicate(root, criteriaBuilder, fieldName, value);
            case LESS_THAN_EQUAL -> buildLessThanEqualPredicate(root, criteriaBuilder, fieldName, value);
            case GREATER_THAN -> buildGreaterThanPredicate(root, criteriaBuilder, fieldName, value);
            case GREATER_THAN_EQUAL -> buildGreaterThanEqualPredicate(root, criteriaBuilder, fieldName, value);
            case IN -> buildInPredicate(root, fieldName, value);
            case LIKE -> buildLikePredicate(root, criteriaBuilder, fieldName, value);
            case DATE_RANGE_OVERLAPS -> buildDateRangeOverlapPredicate(root, criteriaBuilder, fieldName, value);

            default -> {
                log.error("Unsupported operation: {}", operation);
                throw new IllegalArgumentException("Operation is not supported");
            }
        };
    }

    public static Predicate buildEqualPredicate(Root<?> root,
            CriteriaBuilder criteriaBuilder,
            String fieldName, Object value) {
        if (value instanceof String strVal) {
            return criteriaBuilder.equal(
                    criteriaBuilder.lower(root.get(fieldName)),
                    strVal.trim().toLowerCase());
        }
        return criteriaBuilder.equal(root.get(fieldName), value);
    }

    public static Predicate buildNotEqualPredicate(Root<?> root,
            CriteriaBuilder criteriaBuilder,
            String fieldName, Object value) {
        if (value instanceof String strVal) {
            return criteriaBuilder.notEqual(
                    criteriaBuilder.lower(root.get(fieldName)),
                    strVal.toLowerCase());
        }
        return criteriaBuilder.notEqual(root.get(fieldName), value);
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public static Predicate buildLessThanPredicate(Root<?> root,
            CriteriaBuilder criteriaBuilder,
            String fieldName, Object value) {
        if (value instanceof Comparable) {
            return criteriaBuilder.lessThan(root.get(fieldName), (Comparable) value);
        }
        throw new IllegalArgumentException("LESS_THAN operation only supports Comparable values");
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public static Predicate buildLessThanEqualPredicate(Root<?> root,
            CriteriaBuilder criteriaBuilder,
            String fieldName, Object value) {
        if (value instanceof Comparable) {
            return criteriaBuilder.lessThanOrEqualTo(root.get(fieldName), (Comparable) value);
        }
        throw new IllegalArgumentException("LESS_THAN_EQUAL operation only supports Comparable values");
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public static Predicate buildGreaterThanPredicate(Root<?> root,
            CriteriaBuilder criteriaBuilder,
            String fieldName, Object value) {
        if (value instanceof Comparable) {
            return criteriaBuilder.greaterThan(root.get(fieldName), (Comparable) value);
        }
        throw new IllegalArgumentException("GREATER_THAN operation only supports Comparable values");
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public static Predicate buildGreaterThanEqualPredicate(Root<?> root,
            CriteriaBuilder criteriaBuilder,
            String fieldName, Object value) {
        if (value instanceof Comparable) {
            return criteriaBuilder.greaterThanOrEqualTo(root.get(fieldName), (Comparable) value);
        }
        throw new IllegalArgumentException("GREATER_THAN_EQUAL operation only supports Comparable values");
    }

    public static Predicate buildInPredicate(Root<?> root,
            String fieldName, Object value) {
        if (value instanceof Collection<?> collection) {
            return root.get(fieldName).in(collection);
        }
        return root.get(fieldName).in(value);
    }

    public static Predicate buildLikePredicate(Root<?> root,
            CriteriaBuilder criteriaBuilder,
            String fieldName, Object value) {
        String searchPattern = WILD_CARD_ANY_VALUE.formatted(String.valueOf(value).trim()).toLowerCase();
        return criteriaBuilder.like(criteriaBuilder.lower(root.get(fieldName)), searchPattern);
    }

    public static Predicate buildDateRangeOverlapPredicate(Root<?> root,
            CriteriaBuilder criteriaBuilder,
            String startFieldName,
            String endFieldName,
            @Nullable LocalDateTime newStart,
            @Nullable LocalDateTime newEnd) {
        log.debug("Building date range overlap predicate: dbStart={}, dbEnd={}, newStart={}, newEnd={}",
                startFieldName, endFieldName, newStart, newEnd);

        Predicate condition1 = criteriaBuilder.or(
                criteriaBuilder.isNull(root.get(endFieldName)),
                newStart == null ? criteriaBuilder.conjunction()
                        : criteriaBuilder.lessThanOrEqualTo(
                                criteriaBuilder.literal(newStart),
                                root.get(endFieldName).as(LocalDateTime.class)));

        Predicate condition2;
        if (newEnd == null) {
            condition2 = criteriaBuilder.conjunction();
        } else {
            condition2 = criteriaBuilder.lessThanOrEqualTo(
                    root.get(startFieldName).as(LocalDateTime.class),
                    criteriaBuilder.literal(newEnd));
        }

        return criteriaBuilder.and(condition1, condition2);
    }

    public static Predicate buildDateRangeOverlapPredicate(Root<?> root,
            CriteriaBuilder criteriaBuilder,
            String fieldName,
            Object value) {
        if (!(value instanceof DateRangeValue dateRangeValue)) {
            throw new IllegalArgumentException(
                    "OVERLAPS operation requires DateRangeValue object. " +
                            "Use: DateRangeValue.of(startFieldName, endFieldName, startDate, endDate)");
        }

        return buildDateRangeOverlapPredicate(
                root,
                criteriaBuilder,
                dateRangeValue.getStartFieldName(),
                dateRangeValue.getEndFieldName(),
                dateRangeValue.getStart(),
                dateRangeValue.getEnd());
    }

    @Getter
    public static class SearchCriteria {
        private final String fieldName;
        private final Optional<?> optionalValue;
        private final SearchOperation operation;

        private SearchCriteria() {
            this.fieldName = "";
            this.optionalValue = Optional.empty();
            this.operation = UNKNOWN;
        }

        private SearchCriteria(String fieldName, Optional<?> optionalValue, SearchOperation operation) {
            if (operation == LIKE && optionalValue.isPresent() && !(optionalValue.get() instanceof String)) {
                throw new IllegalArgumentException("LIKE operation only supports String values");
            }
            if ((operation == LESS_THAN || operation == GREATER_THAN || operation == LESS_THAN_EQUAL
                    || operation == GREATER_THAN_EQUAL)
                    && optionalValue.isPresent() && !(optionalValue.get() instanceof Comparable)) {
                throw new IllegalArgumentException(
                        "LESS_THAN, GREATER_THAN, LESS_THAN_EQUAL, GREATER_THAN_EQUAL operations only support Comparable values");
            }
            this.fieldName = fieldName;
            this.optionalValue = optionalValue;
            this.operation = operation;
        }

        public static Builder builder() {
            return new Builder();
        }

        public static class Builder {
            private String fieldName = "";
            private Optional<?> optionalValue = Optional.empty();
            private SearchOperation operation = UNKNOWN;

            public Builder fieldName(String fieldName) {
                this.fieldName = fieldName;
                return this;
            }

            public <T> Builder value(@Nullable T value) {
                this.optionalValue = Optional.ofNullable(value);
                return this;
            }

            public Builder operation(SearchOperation operation) {
                this.operation = operation;
                return this;
            }

            public SearchCriteria build() {
                return new SearchCriteria(fieldName, optionalValue, operation);
            }
        }
    }
}