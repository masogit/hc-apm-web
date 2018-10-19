package webapp.framework.dao;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import javaslang.Tuple2;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.domain.Specification;

/**
 *
 * @author wujianb
 */
public class BySearchFilterSpecification {

    public static <T> Specification<T> bySearchFilter(final Collection<SearchFilter> filters, final Class<T> entityClazz) {
        return new Specification<T>() {
            @Override
            public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder builder) {
                if (!filters.isEmpty()) {

                    List<Tuple2<String, Predicate>> predicates = new ArrayList();
                    for (SearchFilter filter : filters) {
                        // nested path translate, eg, task's "user.name" to Task.user.name
                        String[] names = StringUtils.split(filter.fieldName, ".");
                        Path expression = root.get(names[0]);
                        for (int i = 1; i < names.length; i++) {
                            expression = expression.get(names[i]);
                        }

                        // logic operator
                        switch (filter.operator) {
                            case EQ:
                                predicates.add(new Tuple2(filter.conditionPath, builder.equal(expression, filter.value)));
                                break;
                            case NE:
                                predicates.add(new Tuple2(filter.conditionPath, builder.notEqual(expression, filter.value)));
                                break;
                            case LIKE:
                                if(filter.value!=null){
                                    predicates.add(new Tuple2(filter.conditionPath, builder.like(builder.lower(expression), "%" + filter.value.toString().toLowerCase() + "%")));
                                }
                                break;
                            case GT:
                                predicates.add(new Tuple2(filter.conditionPath, builder.greaterThan(expression, (Comparable) filter.value)));
                                break;
                            case LT:
                                predicates.add(new Tuple2(filter.conditionPath, builder.lessThan(expression, (Comparable) filter.value)));
                                break;
                            case GTE:
                                predicates.add(new Tuple2(filter.conditionPath, builder.greaterThanOrEqualTo(expression, (Comparable) filter.value)));
                                break;
                            case LTE:
                                predicates.add(new Tuple2(filter.conditionPath, builder.lessThanOrEqualTo(expression, (Comparable) filter.value)));
                                break;
                            case IN:
                                List list = (List) filter.value;
                                if (list.isEmpty()) {
                                    list.add("");
                                }
                                predicates.add(new Tuple2(filter.conditionPath, expression.in(list)));
                                break;
                            case NotIN:
                                List notInlist = (List) filter.value;
                                if (notInlist.isEmpty()) {
                                    notInlist.add("");
                                }
                                predicates.add(new Tuple2(filter.conditionPath, builder.not(expression.in(notInlist))));
                                break;
                            case IsNull:
                                predicates.add(new Tuple2(filter.conditionPath, expression.isNull()));
                                break;
                            case IsNotNull:
                                predicates.add(new Tuple2(filter.conditionPath, expression.isNotNull()));
                                break;
                        }
                    }

                    // and all predicates
                    if (!predicates.isEmpty()) {
                        return buildConditions(predicates, predicates.get(0)._1, builder);
//                        return builder.and(predicates.toArray(new Predicate[predicates.size()]));
                    }
                }

                return builder.conjunction();
            }

            private Predicate buildConditions(List<Tuple2<String, Predicate>> conditionList, String currentConditions, CriteriaBuilder builder) {
                List<Tuple2<String, Predicate>> currentList = new ArrayList();
                for (Tuple2<String, Predicate> item : conditionList) {
                    if (item._1.startsWith(currentConditions)) {
                        if (item._1.equals(currentConditions)) {
                            currentList.add(item);
                        } else {
                            return buildConditions(conditionList, item._1, builder);
                        }
                    }
                }

                Predicate predicate = null;
                Predicate[] predicateArray = currentList.stream().map(item -> item._2).collect(Collectors.toList()).toArray(new Predicate[currentList.size()]);
                if (currentConditions.endsWith("*")) {
                    predicate = builder.and(predicateArray);
                } else if (currentConditions.endsWith("+")) {
                    predicate = builder.or(predicateArray);
                }
                conditionList.removeAll(currentList);
                do {
                    currentConditions = currentConditions.substring(0, currentConditions.length() - 1);
                } while (!currentConditions.isEmpty() && !currentConditions.endsWith("*") && !currentConditions.endsWith("+"));

                conditionList.add(new Tuple2(currentConditions, predicate));
                if (conditionList.size() == 1) {
                    return predicate;
                } else {
                    return buildConditions(conditionList, currentConditions, builder);
                }

            }
        };
    }
}
