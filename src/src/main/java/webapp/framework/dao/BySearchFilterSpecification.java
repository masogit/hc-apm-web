package webapp.framework.dao;

import javaslang.Tuple2;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.ejb.criteria.path.PluralAttributePath;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author wujianb
 */
public class BySearchFilterSpecification {

    public static <T> Specification<T> bySearchFilter(final Collection<SearchFilter> filters, final Class<T> entityClazz) {
        return new Specification<T>() {
            @Override
            public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder builder) {
                if (!filters.isEmpty()) {

                    List<Tuple2<String, Predicate>> predicates = new ArrayList<>();
                    for (SearchFilter filter : filters) {
                        // nested path translate, eg, task's "user.name" to Task.user.name
                        String[] names = StringUtils.split(filter.fieldName, ".");
                        Path expression = root.get(names[0]);
                        Class clazz = expression.getJavaType();
                        if(clazz.equals(Set.class)){
                            SetJoin setJoin = root.joinSet(names[0],JoinType.LEFT);
//                            query.distinct(true);
                            expression = setJoin.get(names[1]);
                        }else {
                            for (int i = 1; i < names.length; i++) {
                                if (i == 1 && expression instanceof PluralAttributePath) {
                                    expression = root.join(names[0]);
                                }
                                expression = expression.get(names[i]);
                            }
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
                                if (filter.value != null && !filter.value.toString().isEmpty()
                                        && String.class.isAssignableFrom(expression.getJavaType())) {
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
                                Collection list = (Collection) filter.value;
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
                            case IsEmpty:
                                predicates.add(new Tuple2(filter.conditionPath, builder.isEmpty(expression)));
                                break;
                            case CONTAINS:
                                List<String> list2 = (List<String>) filter.value;
                                List<Predicate> PredicateList=new ArrayList<>();
                                for (String s: list2) {
                                    if (s!= null) {
                                        Predicate like = builder.like(expression, "%" + s + "%");
                                        PredicateList.add(like);
                                    }
                                }
                                Predicate[] Predicates = new Predicate[PredicateList.size()];
                                PredicateList.toArray(Predicates);
                                predicates.add(new Tuple2(filter.conditionPath, builder.or(Predicates)));
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
                // Iterative bottom-up approach: process from longest paths to shortest,
                // combining sibling predicates into parent groups until one remains.
                // This replaces the original recursive approach which caused StackOverflow
                // when multiple independent condition groups (e.g. "1*" and "adv*") coexist.
                List<Predicate> rootResults = new ArrayList<>();

                while (conditionList.size() > 1) {
                    // Find the longest path (deepest in hierarchy)
                    String targetPath = conditionList.get(0)._1;
                    for (Tuple2<String, Predicate> item : conditionList) {
                        if (item._1.length() > targetPath.length()) {
                            targetPath = item._1;
                        }
                    }

                    // Collect all predicates with this exact path
                    List<Predicate> matchPredicates = new ArrayList<>();
                    List<Tuple2<String, Predicate>> remaining = new ArrayList<>();
                    for (Tuple2<String, Predicate> item : conditionList) {
                        if (item._1.equals(targetPath)) {
                            matchPredicates.add(item._2);
                        } else {
                            remaining.add(item);
                        }
                    }

                    // Combine predicates at this level
                    Predicate combined;
                    if (targetPath.endsWith("*")) {
                        combined = builder.and(matchPredicates.toArray(new Predicate[0]));
                    } else if (targetPath.endsWith("+")) {
                        combined = builder.or(matchPredicates.toArray(new Predicate[0]));
                    } else {
                        combined = matchPredicates.size() == 1 ? matchPredicates.get(0)
                                : builder.and(matchPredicates.toArray(new Predicate[0]));
                    }

                    // Compute parent path by stripping only ONE level
                    // e.g. "1*2*3+" -> strip "+" -> "1*2*3" -> strip to last "*" -> "1*2*"
                    String parentPath = targetPath;
                    if (parentPath.endsWith("*") || parentPath.endsWith("+")) {
                        parentPath = parentPath.substring(0, parentPath.length() - 1);
                    }
                    while (!parentPath.isEmpty() && !parentPath.endsWith("*") && !parentPath.endsWith("+")) {
                        parentPath = parentPath.substring(0, parentPath.length() - 1);
                    }

                    if (!parentPath.isEmpty()) {
                        // Check if parent path exists in remaining items
                        boolean parentExists = false;
                        for (Tuple2<String, Predicate> item : remaining) {
                            if (item._1.equals(parentPath)) {
                                parentExists = true;
                                break;
                            }
                        }
                        if (parentExists) {
                            // Bubble up: add combined predicate under parent path for further processing
                            remaining.add(new Tuple2<>(parentPath, combined));
                            conditionList = remaining;
                        } else {
                            // Parent not in list - this is a root-level result
                            rootResults.add(combined);
                            conditionList = remaining;
                        }
                    } else {
                        // No parent (root-level group like "1*" or "adv*") - store as result
                        rootResults.add(combined);
                        conditionList = remaining;
                    }
                }

                // Handle last remaining item
                if (conditionList.size() == 1) {
                    rootResults.add(conditionList.get(0)._2);
                }

                if (rootResults.isEmpty()) {
                    return builder.conjunction();
                }
                if (rootResults.size() == 1) {
                    return rootResults.get(0);
                }
                // Multiple independent root groups are ANDed together
                return builder.and(rootResults.toArray(new Predicate[0]));
            }
        };
    }
}
