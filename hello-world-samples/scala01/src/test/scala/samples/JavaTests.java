package samples;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@SuppressWarnings("unused")
public class JavaTests {


    @Test
    void streamsTest() {

        Double average = Stream.of(1, 2, 3)
                .map(v -> v * 10)
                .collect(Collectors.averagingDouble(Integer::doubleValue));

        String veryBadUsage = Stream.of(1, 2, 3)
                .map(v -> v * 10)
                .reduce(
                        new StringBuilder(),
                        (v1, v2) -> new StringBuilder().append(v1).append(" ").append(v2),
                        (v1, v2) -> new StringBuilder().append(v1).append(" ").append(v2)
                ).toString();

        /*
        Stream.of(1, 2, 3)
                .map(v -> v * 10)
                //.collect(Collectors.toCollection(() -> new StringBuilder()));
                .collect(Collector.of(() -> new StringBuilder(), ));

        Object v = new Collectors.CollectorImpl<CharSequence, StringBuilder, String>(
                StringBuilder::new, StringBuilder::append,
                (r1, r2) -> { r1.append(r2); return r1; },
                StringBuilder::toString, Collections.emptySet());

        Object v22 = Collector.<CharSequence, StringBuilder>of(
                null,
                null,
                null);
        */

        String sss = Stream.of(1, 2, 3)
                .map(v -> v * 10)
                .map(Object::toString)
                .collect(Collector.<CharSequence, StringBuilder>of(
                        StringBuilder::new,
                        StringBuilder::append,
                        (r1, r2) -> { r1.append(r2); return r1; }))
                .toString();
        System.out.println(sss);

        String sss2 = Stream.of(1, 2, 3)
                .map(v -> v * 10)
                .map(Object::toString)
                .collect(Collector.of(
                        StringBuilder::new,
                        StringBuilder::append,
                        (r1, r2) -> { r1.append(r2); return r1; }))
                .toString();
        System.out.println(sss2);

        String sss3 = Stream.of(1, 2, 3)
                .map(v -> v * 10)
                .map(Object::toString)
                .collect(Collector.of(
                        StringBuilder::new,
                        StringBuilder::append,
                        (r1, r2) -> { r1.append(r2); return r1; },
                        StringBuilder::toString));
        System.out.println(sss3);

    }

    @Test
    @Disabled // for manual running
    void testSorting() {

        Random rnd = new Random();

        long start0 = System.nanoTime();
        List<Integer> values = IntStream.range(0, 10_000_000)
                .map(bound -> rnd.nextInt())
                .boxed()
                .collect(Collectors.toCollection(LinkedList::new));
        long end0 = System.nanoTime();

        long start = System.nanoTime();
        values.sort(Integer::compare);
        long end = System.nanoTime();

        System.out.println("sort time: " + (end - start)/1000_000);
        System.out.println("prepare time: " + (end0 - start0)/1000_000);
    }

    @Test
    void testAAA() {
        assertThat(areEqual(21, 21)).isEqualTo(true);
        // by default boxing is cached only for -128..127
        //assertThat(areEqual(421, 421)).isEqualTo(true);
    }

    @SuppressWarnings({"NumberEquality", "SameParameterValue"})
    private static boolean areEqual(Integer v1, Integer v2) {
        return v1 == v2;
    }

}
