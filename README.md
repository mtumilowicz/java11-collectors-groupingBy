# java11-collectors-groupingBy

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
