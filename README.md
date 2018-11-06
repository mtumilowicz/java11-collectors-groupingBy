[![Build Status](https://travis-ci.com/mtumilowicz/java11-collectors-groupingBy.svg?branch=master)](https://travis-ci.com/mtumilowicz/java11-collectors-groupingBy)

# java11-collectors-groupingBy
Summary of `Collectors.groupingBy` API.

_Reference_: Java8 - https://www.baeldung.com/java-groupingby-collector  
_Reference_: Java9 - https://www.baeldung.com/java9-stream-collectors  
_Reference_: Java9 - http://www.deadcoderising.com/java-9-filtering-and-flatmapping-two-new-collectors-for-your-streams/

# preface
## java 8
```
public static <T, K> 
    Collector<T, ?, Map<K, List<T>>>
    groupingBy(Function<? super T, ? extends K> classifier)
```
```
public static <T, K, A, D>
    Collector<T, ?, Map<K, D>> 
    groupingBy(Function<? super T, ? extends K> classifier,
               Collector<? super T, A, D> downstream)
```
```
public static <T, K, D, A, M extends Map<K, D>>
    Collector<T, ?, M> 
    groupingBy(Function<? super T, ? extends K> classifier,
               Supplier<M> mapFactory,
               Collector<? super T, A, D> downstream)
```
where:
* **classifier** - a classifier function mapping input elements to keys
* **downstream** - a `Collector` implementing the downstream reduction,
for example:
    * `Collectors.averagingInt(ToIntFunction<? super T> mapper)`
    * `Collectors.summingInt(ToIntFunction<? super T> mapper)`
    * `Collectors.toList()`
    * `Collectors.toSet()`
    * `Collectors.mapping(Function<? super T, ? extends U> mapper, Collector<? super U, A, R> downstream)`
    * `Collectors.groupingBy(...)`
    * `Collectors.reducing(...)`
    * `Collectors.collectingAndThen(Collector<T,A,R> downstream, Function<R,RR> finisher)` - please refer my other
    github project: [Collectors.collectingAndThen](https://github.com/mtumilowicz/java11-collectors-collectingAndThen)
    * `Collectors.toMap(...)` - please refer my other github project: 
    [Collectors.toMap](https://github.com/mtumilowicz/java11-collectors-tomap)
* **mapFactory** - a supplier providing a new empty `Map` into which the 
results will be inserted

## java 9
In java 9 we have two more collectors:
```
public static <T, A, R>
    Collector<T, ?, R> 
    filtering(Predicate<? super T> predicate,
              Collector<? super T, A, R> downstream)
```
```
public static <T, U, A, R>
    Collector<T, ?, R> 
    flatMapping(Function<? super T, ? extends Stream<? extends U>> mapper,
                Collector<? super U, A, R> downstream)
```

# project description
* Entity
    ```
    @Value
    @Builder
    class Person {
        int id;
        String jobTitle;
        int age;
        Integer salary;
        
        boolean isOlderThan(int value) {
            return age > value;
        }
    }
    ```
* we want to perform grouping:
    * `Map<String, List<Person>>`: persons grouped by jobTitle (to list)
        ```
        stream.collect(groupingBy(Person::getJobTitle));        
        ```
    * `Map<String, Set<Person>>`: persons grouped by jobTitle (to set)
        ```
        stream.collect(groupingBy(Person::getJobTitle, toSet()));
        ```
    * `Map<String, List<Integer>>`: age of persons grouped by jobTitle 
    (to list)
        ```
        stream.collect(groupingBy(Person::getJobTitle, mapping(Person::getAge, toList())));        
        ```
    * `TreeMap<String, Set<Person>>`: persons grouped by jobTitle 
    (to list) in a tree map with reversed order
        ```
        stream.collect(groupingBy(Person::getJobTitle, () -> new TreeMap<>(Comparator.reverseOrder()), toSet()));        
        ```
    * `Map<String, List<Person>>`: persons grouped by jobTitle and filtered by age > 30
        ```
        stream.collect(groupingBy(Person::getJobTitle, filtering(person -> person.isOlderThan(30), toList())));
        ```
        **Remark**:
        * if we `filter` stream before collection we **lose irretrievably** 
        entries
        * if we `filter` stream after collection we have keys with empty values
    * `Map<String, Long>`: group by jobTitle and count every group
        ```
        stream.collect(groupingBy(Person::getJobTitle, counting()));
        ```
    * `Map<String, Map<Integer, Long>>`: group by jobTitle then group by age and count every group
        ```
        stream.collect(groupingBy(Person::getJobTitle, groupingBy(Person::getAge, counting())));
        ```
    * `Map<String, Double>`: group by jobTitle and count average salary for every group
        ```
        stream.collect(groupingBy(Person::getJobTitle, averagingInt(Person::getSalary)));
        ```
    * `Map<String, Optional<Integer>>`: group persons by jobTitle and find max salary for every group
        ```
        stream.collect(groupingBy(Person::getJobTitle, mapping(Person::getSalary, maxBy(Comparator.comparingInt(Integer::intValue)))));
        ```
        **Remark** - using maxBy + comparator is not sufficient:
        ```
        Map<String, Optional<Person>> collect = stream
                .collect(groupingBy(Person::getJobTitle, maxBy(Comparator.comparingInt(Person::getSalary))));        
        ```
    * `Map<String, Integer>`: group persons by jobTitle and find max age for every group 
    (if there is no max value -> put `-1`)
        ```
        stream.collect(groupingBy(Person::getJobTitle,
                                       mapping(Person::getAge,
                                               collectingAndThen(
                                                       maxBy(Comparator.comparingInt(Integer::intValue)),
                                                       optional -> optional.orElse(-1)
                                               )
                                       )));
        ```
    * `Map<String, List<String>>`: find all hobbies for jobTitle
        ```
        stream.collect(groupingBy(Person::getJobTitle,
                                  Collectors.flatMapping(Person::getHobbiesAsStream, toList())));
        ```
        **Remark** - without flatMapping it would be impossible, because:
        ```
        Map<String, List<List<String>>> map = stream
                .collect(groupingBy(Person::getJobTitle,
                        Collectors.mapping(Person::getHobbies, toList())));        
        ```
    * `Map<String, Optional<String>>`: find all hobbies for jobTitle and concat them separated by comma
        ```
        stream.collect(groupingBy(Person::getJobTitle,
                                Collectors.flatMapping(Person::getHobbiesAsStream,
                                        reducing((x, y) -> x + "," + y))));
        ```