import org.junit.Test;

import java.util.*;
import java.util.stream.Collectors;
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

        Map<String, List<Person>> jobTitlePersonMap = Stream.of(p1, p2)
                .collect(groupingBy(Person::getJobTitle));

        assertThat(jobTitlePersonMap.size(), is(1));
        assertThat(jobTitlePersonMap.get("manager"), hasSize(2));
        assertThat(jobTitlePersonMap.get("manager"), containsInAnyOrder(p1, p2));
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

        Map<String, Set<Person>> jobTitlePersonMap = Stream.of(p1, p2)
                .collect(groupingBy(Person::getJobTitle, toSet()));

        assertThat(jobTitlePersonMap.size(), is(1));
        assertThat(jobTitlePersonMap.get("manager"), hasSize(1));
        assertThat(jobTitlePersonMap.get("manager"), contains(p1));
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

        Map<String, List<Integer>> jobTitleAgeMap = Stream.of(p1, p2)
                .collect(groupingBy(Person::getJobTitle,
                        mapping(Person::getAge, toList())));

        assertThat(jobTitleAgeMap.size(), is(1));
        assertThat(jobTitleAgeMap.get("manager"), hasSize(2));
        assertThat(jobTitleAgeMap.get("manager"), contains(20, 35));
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

        TreeMap<String, Set<Person>> jobTitlePersonMap = Stream.of(p1, p2, p3)
                .collect(groupingBy(Person::getJobTitle, () -> new TreeMap<>(Comparator.reverseOrder()), toSet()));

        assertThat(jobTitlePersonMap.size(), is(2));

        assertThat(jobTitlePersonMap.firstEntry().getValue(), hasSize(1));
        assertThat(jobTitlePersonMap.firstEntry().getValue(), containsInAnyOrder(p3));

        assertThat(jobTitlePersonMap.lastEntry().getValue(), hasSize(2));
        assertThat(jobTitlePersonMap.lastEntry().getValue(), containsInAnyOrder(p1, p2));
    }

    @Test
    public void groupByJobTitle_countEveryGroup() {
        var p1 = Person.builder()
                .id(1)
                .jobTitle("manager")
                .build();
        var p2 = Person.builder()
                .id(2)
                .jobTitle("manager")
                .build();

        Map<String, Long> jobTitlePersonMap = Stream.of(p1, p2)
                .collect(groupingBy(Person::getJobTitle, counting()));

        assertThat(jobTitlePersonMap.size(), is(1));
        assertThat(jobTitlePersonMap.get("manager"), is(2L));
    }

    @Test
    public void groupByJobTitle_groupByAge_countEveryGroup() {
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

        Map<String, Map<Integer, Long>> jobTitlePersonMap = Stream.of(p1, p2, p3, p4)
                .collect(groupingBy(Person::getJobTitle, groupingBy(Person::getAge, counting())));

        assertThat(jobTitlePersonMap.size(), is(2));

        assertThat(jobTitlePersonMap.get("manager").get(10), is(2L));
        assertThat(jobTitlePersonMap.get("manager").get(15), is(1L));

        assertThat(jobTitlePersonMap.get("assistant").get(20), is(1L));
    }

    @Test
    public void groupByJobTitle_averageSalary() {
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

        Map<String, Double> jobTitleAverageSalaryMap = Stream.of(p1, p2)
                .collect(groupingBy(Person::getJobTitle, averagingInt(Person::getSalary)));

        assertThat(jobTitleAverageSalaryMap.size(), is(1));
        assertThat(jobTitleAverageSalaryMap.get("manager"), is(15d));
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

        Map<String, List<Person>> jobTitleAgeMap = Stream.of(p1, p2, p3)
                .filter(person -> person.isOlderThan(30))
                .collect(groupingBy(Person::getJobTitle));

        assertThat(jobTitleAgeMap.size(), is(1));
        assertThat(jobTitleAgeMap.get("manager"), hasSize(1));
        assertThat(jobTitleAgeMap.get("manager"), contains(p1));
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

        Map<String, Optional<Integer>> jobTitleMaxSalaryMap = Stream.of(p1, p2, p3)
                .collect(groupingBy(Person::getJobTitle,
                        mapping(Person::getSalary, maxBy(Comparator.comparingInt(Integer::intValue))
                        )));

        assertThat(jobTitleMaxSalaryMap.size(), is(2));

        assertThat(jobTitleMaxSalaryMap.get("manager").orElseThrow(), is(40));
        assertTrue(jobTitleMaxSalaryMap.get("developer").isEmpty());
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

        Map<String, Integer> jobTitleMaxAgeMap = Stream.of(p1, p2)
                .collect(groupingBy(Person::getJobTitle,
                        mapping(Person::getAge,
                                collectingAndThen(
                                        maxBy(Comparator.comparingInt(Integer::intValue)),
                                        optional -> optional.orElse(-1)
                                )
                        )));

        assertThat(jobTitleMaxAgeMap.size(), is(1));
        assertThat(jobTitleMaxAgeMap.get("manager"), is(15));
    }

    @Test
    public void findAllHobbiesForEveryGroupByJobTitle() {
        var p1 = Person.builder()
                .id(1)
                .jobTitle("manager")
                .hobbies(Arrays.asList("skiing", "football"))
                .build();
        var p2 = Person.builder()
                .id(2)
                .jobTitle("manager")
                .hobbies(Arrays.asList("music", "films"))
                .build();
        var p3 = Person.builder()
                .id(2)
                .jobTitle("developer")
                .hobbies(Arrays.asList("RPG", "comics"))
                .build();

        Map<String, List<List<String>>> map = Stream.of(p1, p2, p3)
                .collect(groupingBy(Person::getJobTitle,
                        Collectors.mapping(Person::getHobbies, toList())));

        Map<String, List<String>> jobTitleHobbiesMap = Stream.of(p1, p2, p3)
                .collect(groupingBy(Person::getJobTitle,
                        Collectors.flatMapping(Person::getHobbiesAsStream, toList())));

        assertThat(jobTitleHobbiesMap.size(), is(2));

        assertThat(jobTitleHobbiesMap.get("manager"), containsInAnyOrder(
                "skiing",
                "football",
                "music",
                "films"));

        assertThat(jobTitleHobbiesMap.get("developer"), containsInAnyOrder("RPG", "comics"));
    }
}
