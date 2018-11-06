# java11-collectors-groupingBy
Summary of Collectors.groupingBy API.

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

## java 9
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
        String name;
        int age;
        Integer salary;
        
        boolean isOlderThan(int value) {
            return age > value;
        }
    }
    ```
* we want to perform grouping:
    * `Map<String, List<Person>>`: persons grouped by name (to list)
        ```
        stream.collect(groupingBy(Person::getName));        
        ```
    * `Map<String, Set<Person>>`: persons grouped by name (to set)
        ```
        stream.collect(groupingBy(Person::getName, toSet()));
        ```
    * `Map<String, List<Integer>>`: age of persons grouped by name 
    (to list)
        ```
        stream.collect(Collectors.groupingBy(Person::getName, Collectors.mapping(Person::getAge, Collectors.toList())));        
        ```
    * `TreeMap<String, Set<Person>>`: persons grouped by name 
    (to list) in a tree map with reversed order
        ```
        stream.collect(groupingBy(Person::getName, () -> new TreeMap<>(Comparator.reverseOrder()), toSet()));        
        ```
    * `Map<String, List<Person>>`: persons grouped by name and filtered by age > 30
        ```
        stream.collect(groupingBy(Person::getName, filtering(person -> person.isOlderThan(30), toList())));
        ```
        _Remark_: difference to filter on stream
    * `Map<String, Optional<Integer>>`: group persons by name and find max salary for every group
        ```
        stream.collect(groupingBy(Person::getName, mapping(Person::getSalary, maxBy(Comparator.comparingInt(Integer::intValue)))));
        ```
    * `Map<String, Optional<Integer>>`: group persons by name and find max age for every group 
    (if there is no max value -> put `-1`)
        ```
        stream.collect(groupingBy(Person::getName,
                                       mapping(Person::getAge,
                                               collectingAndThen(
                                                       maxBy(Comparator.comparingInt(Integer::intValue)),
                                                       optional -> optional.orElse(-1)
                                               )
                                       )));
        ```