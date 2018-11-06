import lombok.Builder;
import lombok.Value;
import org.apache.commons.collections4.ListUtils;

import java.util.List;
import java.util.stream.Stream;

/**
 * Created by mtumilowicz on 2018-11-04.
 */
@Value
@Builder
class Person {
    int id;
    String jobTitle;
    int age;
    Integer salary;
    List<String> hobbies;
    
    boolean isOlderThan(int value) {
        return age > value;
    }
    
    Stream<String> getHobbiesAsStream() {
        return ListUtils.emptyIfNull(hobbies).stream();
    }
}
