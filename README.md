# java11-collectors-groupingBy

_Reference_: Java8 - https://www.baeldung.com/java-groupingby-collector  
_Reference_: Java9 - https://www.baeldung.com/java9-stream-collectors  
_Reference_: Java9 - http://www.deadcoderising.com/java-9-filtering-and-flatmapping-two-new-collectors-for-your-streams/

Collectors.groupingBy
Map<String, List<B>>
collect(Collectors.groupingBy(B::getA));

Map<String, Set<B>>
collect(Collectors.groupingBy(B::getA, Collectors.toSet()));

Map<String, Set<String>>
collect(Collectors.groupingBy(B::getA,Collectors.mapping(B::getA, Collectors.toSet())));

HashMap<String, Set<B>> collect
collect(Collectors.groupingBy(B::getA, HashMap::new, Collectors.toSet()));

collect(Collectors.groupingBy(B::getA, HashMap::new)); // COMPILE-TIME ERROR
