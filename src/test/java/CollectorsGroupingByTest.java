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
                .jobTitle("manager")
                .build();
        var p2 = Person.builder()
                .id(2)
                .jobTitle("manager")
                .build();

        Map<String, List<Person>> namePersonMap = Stream.of(p1, p2)
                .collect(groupingBy(Person::getJobTitle));

        assertThat(namePersonMap.size(), is(1));
        assertThat(namePersonMap.get("manager"), hasSize(2));
        assertThat(namePersonMap.get("manager"), containsInAnyOrder(p1, p2));
    }

    @Test
    public void changeTypeOfAnAggregate() {
        var p1 = Person.builder()
                .id(1)
                .jobTitle("manager")
                .build();
        var p2 = Person.builder()
                .id(1)
                .jobTitle("manager")
                .build();

        Map<String, Set<Person>> namePersonMap = Stream.of(p1, p2)
                .collect(groupingBy(Person::getJobTitle, toSet()));

        assertThat(namePersonMap.size(), is(1));
        assertThat(namePersonMap.get("manager"), hasSize(1));
        assertThat(namePersonMap.get("manager"), contains(p1));
    }

    @Test
    public void changeElementTypeOfAnAggregate() {
        var p1 = Person.builder()
                .id(1)
                .jobTitle("manager")
                .age(20)
                .build();
        var p2 = Person.builder()
                .id(2)
                .jobTitle("manager")
                .age(35)
                .build();

        Map<String, List<Integer>> nameAgeMap = Stream.of(p1, p2)
                .collect(groupingBy(Person::getJobTitle,
                        mapping(Person::getAge, toList())));

        assertThat(nameAgeMap.size(), is(1));
        assertThat(nameAgeMap.get("manager"), hasSize(2));
        assertThat(nameAgeMap.get("manager"), contains(20, 35));
    }

    @Test
    public void specificImplementationOfMap() {
        var p1 = Person.builder()
                .id(1)
                .jobTitle("manager")
                .build();
        var p2 = Person.builder()
                .id(2)
                .jobTitle("manager")
                .build();
        var p3 = Person.builder()
                .id(3)
                .jobTitle("president")
                .build();

        TreeMap<String, Set<Person>> namePersonMap = Stream.of(p1, p2, p3)
                .collect(groupingBy(Person::getJobTitle, () -> new TreeMap<>(Comparator.reverseOrder()), toSet()));

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
                .jobTitle("manager")
                .build();
        var p2 = Person.builder()
                .id(2)
                .jobTitle("manager")
                .build();

        Map<String, Long> namePersonMap = Stream.of(p1, p2)
                .collect(groupingBy(Person::getJobTitle, counting()));

        assertThat(namePersonMap.size(), is(1));
        assertThat(namePersonMap.get("manager"), is(2L));
    }
    
    @Test
    public void groupByName_groupByAge_countEveryGroup() {
        var p1 = Person.builder()
                .id(1)
                .jobTitle("manager")
                .age(10)
                .build();
        var p2 = Person.builder()
                .id(2)
                .jobTitle("manager")
                .age(10)
                .build();
        var p3 = Person.builder()
                .id(3)
                .jobTitle("manager")
                .age(15)
                .build();
        var p4 = Person.builder()
                .id(4)
                .jobTitle("assistant")
                .age(20)
                .build();

        Map<String, Map<Integer, Long>> namePersonMap = Stream.of(p1, p2, p3, p4)
                .collect(groupingBy(Person::getJobTitle, groupingBy(Person::getAge, counting())));

        assertThat(namePersonMap.size(), is(2));
        
        assertThat(namePersonMap.get("manager").get(10), is(2L));
        assertThat(namePersonMap.get("manager").get(15), is(1L));
        
        assertThat(namePersonMap.get("assistant").get(20), is(1L));
    }

    @Test
    public void groupByName_averageSalary() {
        var p1 = Person.builder()
                .id(1)
                .jobTitle("manager")
                .salary(10)
                .build();
        var p2 = Person.builder()
                .id(2)
                .jobTitle("manager")
                .salary(20)
                .build();

        Map<String, Double> nameAverageSalaryMap = Stream.of(p1, p2)
                .collect(groupingBy(Person::getJobTitle, averagingInt(Person::getSalary)));

        assertThat(nameAverageSalaryMap.size(), is(1));
        assertThat(nameAverageSalaryMap.get("manager"), is(15d));
    }

    @Test
    public void filtering_before_collection() {
        var p1 = Person.builder()
                .id(1)
                .jobTitle("manager")
                .age(50)
                .build();
        var p2 = Person.builder()
                .id(2)
                .jobTitle("manager")
                .age(20)
                .build();
        var p3 = Person.builder()
                .id(2)
                .jobTitle("developer")
                .age(20)
                .build();

        Map<String, List<Person>> nameAgeMap = Stream.of(p1, p2, p3)
                .filter(person -> person.isOlderThan(30))
                .collect(groupingBy(Person::getJobTitle));

        assertThat(nameAgeMap.size(), is(1));
        assertThat(nameAgeMap.get("manager"), hasSize(1));
        assertThat(nameAgeMap.get("manager"), contains(p1));
    }

    @Test
    public void filtering_after_collection() {
        var p1 = Person.builder()
                .id(1)
                .jobTitle("manager")
                .age(50)
                .build();
        var p2 = Person.builder()
                .id(2)
                .jobTitle("manager")
                .age(20)
                .build();
        var p3 = Person.builder()
                .id(3)
                .jobTitle("developer")
                .age(20)
                .build();

        Map<String, List<Person>> collect = Stream.of(p1, p2, p3)
                .collect(groupingBy(Person::getJobTitle,
                        filtering(person -> person.isOlderThan(30), toList())));

        assertThat(collect.size(), is(2));
        
        assertThat(collect.get("manager"), hasSize(1));
        assertThat(collect.get("manager"), contains(p1));
        
        assertThat(collect.get("developer"), is(empty()));
    }

    @Test
    public void find_max_salary_for_every_group() {
        var p1 = Person.builder()
                .id(1)
                .jobTitle("manager")
                .salary(40)
                .build();
        var p2 = Person.builder()
                .id(2)
                .jobTitle("manager")
                .salary(30)
                .build();
        var p3 = Person.builder()
                .id(3)
                .jobTitle("developer")
                .build();

        Map<String, Optional<Integer>> nameMaxSalaryMap = Stream.of(p1, p2, p3)
                .collect(groupingBy(Person::getJobTitle,
                        mapping(Person::getSalary, maxBy(Comparator.comparingInt(Integer::intValue))
                        )));

        assertThat(nameMaxSalaryMap.size(), is(2));
        
        assertThat(nameMaxSalaryMap.get("manager").orElseThrow(), is(40));
        assertTrue(nameMaxSalaryMap.get("developer").isEmpty());
    }

    @Test
    public void find_max_age_orDefault_for_every_group() {
        var p1 = Person.builder()
                .id(1)
                .jobTitle("manager")
                .age(10)
                .build();
        var p2 = Person.builder()
                .id(2)
                .jobTitle("manager")
                .age(15)
                .build();

        Map<String, Integer> nameMaxAgeMap = Stream.of(p1, p2)
                .collect(groupingBy(Person::getJobTitle,
                        mapping(Person::getAge,
                                collectingAndThen(
                                        maxBy(Comparator.comparingInt(Integer::intValue)),
                                        optional -> optional.orElse(-1)
                                )
                        )));

        assertThat(nameMaxAgeMap.size(), is(1));
        assertThat(nameMaxAgeMap.get("manager"), is(15));
    }
}
