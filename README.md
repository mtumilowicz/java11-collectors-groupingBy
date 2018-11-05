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