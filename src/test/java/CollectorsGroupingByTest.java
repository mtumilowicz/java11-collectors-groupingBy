import org.junit.Test;

import java.util.*;
import java.util.stream.Stream;

import static java.util.stream.Collectors.*;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertTrue;

/**
 * Created by mtumilowicz on 2018-11-05.
 */
public class CollectorsGroupingByTest {

    @Test
    public void defaultCollecting() {
        var p1 = Person.builder()
                .id(1)
                .name("name")
                .build();
        var p2 = Person.builder()
                .id(2)
                .name("name")
                .build();

        Map<String, List<Person>> namePersonMap = Stream.of(p1, p2)
                .collect(groupingBy(Person::getName));

        assertThat(namePersonMap.size(), is(1));
        assertThat(namePersonMap.get("name"), hasSize(2));
        assertThat(namePersonMap.get("name"), containsInAnyOrder(p1, p2));
    }

    @Test
    public void changeTypeOfAnAggregate() {
        var p1 = Person.builder()
                .id(1)
                .name("name")
                .build();
        var p2 = Person.builder()
                .id(2)
                .name("name")
                .build();

        Map<String, Set<Person>> namePersonMap = Stream.of(p1, p2)
                .collect(groupingBy(Person::getName, toSet()));

        assertThat(namePersonMap.size(), is(1));
        assertThat(namePersonMap.get("name"), hasSize(1));
        assertThat(namePersonMap.get("name"), contains(p1));
    }

    @Test
    public void changeElementTypeOfAnAggregate() {
        var p1 = Person.builder()
                .id(1)
                .name("name")
                .age(20)
                .build();
        var p2 = Person.builder()
                .id(2)
                .name("name")
                .age(35)
                .build();

        Map<String, List<Integer>> nameAgeMap = Stream.of(p1, p2)
                .collect(groupingBy(Person::getName,
                        mapping(Person::getAge, toList())));

        assertThat(nameAgeMap.size(), is(1));
        assertThat(nameAgeMap.get("name"), hasSize(2));
        assertThat(nameAgeMap.get("name"), contains(20, 35));
    }

    @Test
    public void specificImplementationOfMap() {
        var p1 = Person.builder()
                .id(1)
                .name("name")
                .build();
        var p2 = Person.builder()
                .id(2)
                .name("name")
                .build();
        var p3 = Person.builder()
                .id(3)
                .name("name3")
                .build();

        TreeMap<String, Set<Person>> namePersonMap = Stream.of(p1, p2, p3)
                .collect(groupingBy(Person::getName, () -> new TreeMap<>(Comparator.reverseOrder()), toSet()));

        assertThat(namePersonMap.size(), is(2));
        
        assertThat(namePersonMap.firstEntry().getValue(), hasSize(1));
        assertThat(namePersonMap.firstEntry().getValue(), containsInAnyOrder(p3));

        assertThat(namePersonMap.lastEntry().getValue(), hasSize(2));
        assertThat(namePersonMap.lastEntry().getValue(), containsInAnyOrder(p1, p2));
    }
    
    @Test
    public void groupByName_countEveryGroup() {
        var p1 = Person.builder()
                .id(1)
                .name("name")
                .build();
        var p2 = Person.builder()
                .id(2)
                .name("name")
                .build();

        Map<String, Long> namePersonMap = Stream.of(p1, p2)
                .collect(groupingBy(Person::getName, counting()));

        assertThat(namePersonMap.size(), is(1));
        assertThat(namePersonMap.get("name"), is(2L));
    }

    @Test
    public void filtering_before_collection() {
        var p1 = Person.builder()
                .id(1)
                .name("name")
                .age(50)
                .build();
        var p2 = Person.builder()
                .id(2)
                .name("name")
                .age(20)
                .build();
        var p3 = Person.builder()
                .id(2)
                .name("name3")
                .age(20)
                .build();

        Map<String, List<Person>> nameAgeMap = Stream.of(p1, p2, p3)
                .filter(person -> person.isOlderThan(30))
                .collect(groupingBy(Person::getName));

        assertThat(nameAgeMap.size(), is(1));
        assertThat(nameAgeMap.get("name"), hasSize(1));
        assertThat(nameAgeMap.get("name"), contains(p1));
    }

    @Test
    public void filtering_after_collection() {
        var p1 = Person.builder()
                .id(1)
                .name("name")
                .age(50)
                .build();
        var p2 = Person.builder()
                .id(2)
                .name("name")
                .age(20)
                .build();
        var p3 = Person.builder()
                .id(3)
                .name("name3")
                .age(20)
                .build();

        Map<String, List<Person>> collect = Stream.of(p1, p2, p3)
                .collect(groupingBy(Person::getName,
                        filtering(person -> person.isOlderThan(30), toList())));

        assertThat(collect.size(), is(2));
        
        assertThat(collect.get("name"), hasSize(1));
        assertThat(collect.get("name"), contains(p1));
        
        assertThat(collect.get("name3"), is(empty()));
    }

    @Test
    public void find_max_salary_for_every_group() {
        var p1 = Person.builder()
                .id(1)
                .name("name")
                .salary(40)
                .build();
        var p2 = Person.builder()
                .id(2)
                .name("name")
                .salary(30)
                .build();
        var p3 = Person.builder()
                .id(3)
                .name("name3")
                .build();

        Map<String, Optional<Integer>> nameMaxSalaryMap = Stream.of(p1, p2, p3)
                .collect(groupingBy(Person::getName,
                        mapping(Person::getSalary, maxBy(Comparator.comparingInt(Integer::intValue))
                        )));

        assertThat(nameMaxSalaryMap.size(), is(2));
        
        assertThat(nameMaxSalaryMap.get("name").orElseThrow(), is(40));
        assertTrue(nameMaxSalaryMap.get("name3").isEmpty());
    }

    @Test
    public void find_max_age_orDefault_for_every_group() {
        var p1 = Person.builder()
                .id(1)
                .name("name")
                .age(10)
                .build();
        var p2 = Person.builder()
                .id(2)
                .name("name")
                .age(15)
                .build();

        Map<String, Integer> nameMaxAgeMap = Stream.of(p1, p2)
                .collect(groupingBy(Person::getName,
                        mapping(Person::getAge,
                                collectingAndThen(
                                        maxBy(Comparator.comparingInt(Integer::intValue)),
                                        optional -> optional.orElse(-1)
                                )
                        )));

        assertThat(nameMaxAgeMap.size(), is(1));
        assertThat(nameMaxAgeMap.get("name"), is(15));
    }
}
