import org.junit.Test;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasSize;

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

        Map<String, List<Person>> collect = Stream.of(p1, p2)
                .collect(Collectors.groupingBy(Person::getName));
        assertThat(collect.size(), is(1));
        assertThat(collect.get("name"), hasSize(2));
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

        Map<String, Set<Person>> collect = Stream.of(p1, p2)
                .collect(Collectors.groupingBy(Person::getName, Collectors.toSet()));
        
        assertThat(collect.size(), is(1));
        assertThat(collect.get("name"), hasSize(2));
    }

    @Test
    public void changeElementTypeOfAnAggregate() {
        var p1 = Person.builder()
                .id(1)
                .name("name")
                .build();
        var p2 = Person.builder()
                .id(2)
                .name("name")
                .build();

        Map<String, List<Integer>> collect = Stream.of(p1, p2)
                .collect(Collectors.groupingBy(Person::getName, 
                        Collectors.mapping(Person::getAge, Collectors.toList())));

        assertThat(collect.size(), is(1));
        assertThat(collect.get("name"), hasSize(2));
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

        TreeMap<String, Set<Person>> collect = Stream.of(p1, p2)
                .collect(Collectors.groupingBy(Person::getName, TreeMap::new, Collectors.toSet()));
        
        assertThat(collect.size(), is(1));
        assertThat(collect.get("name"), hasSize(2));
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

        Map<String, List<Person>> collect = Stream.of(p1, p2, p3)
                .filter(person -> person.isOlderThan(30))
                .collect(Collectors.groupingBy(Person::getName));

        assertThat(collect.size(), is(1));
        assertThat(collect.get("name"), hasSize(1));
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
                .id(2)
                .name("name3")
                .age(20)
                .build();

        Map<String, List<Person>> collect = Stream.of(p1, p2, p3)
                .collect(Collectors.groupingBy(Person::getName, 
                        Collectors.filtering(person -> person.isOlderThan(30), Collectors.toList())));
        
        assertThat(collect.size(), is(2));
        assertThat(collect.get("name"), hasSize(1));
        assertThat(collect.get("name3"), is(empty()));
    }
}
