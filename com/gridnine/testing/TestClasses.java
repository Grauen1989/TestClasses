package com.gridnine.testing;

import org.w3c.dom.ls.LSOutput;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Factory class to get sample list of flights (Заводской класс, чтобы получить примерный список рейсов).
 */
class FlightBuilder {                                      // Строитель Полетов
    static List<Flight> createFlights() {
        LocalDateTime threeDaysFromNow = LocalDateTime.now().plusDays(3); // threeDaysFromNow - через три дня
        return Arrays.asList(
            //A normal flight with two hour duration (Обычный полет продолжительностью два часа)
            createFlight(threeDaysFromNow, threeDaysFromNow.plusHours(2)),
            //A normal multi segment flight (Нормальная нескольких полетных сегментов)
            createFlight(threeDaysFromNow, threeDaysFromNow.plusHours(2),
                    threeDaysFromNow.plusHours(3), threeDaysFromNow.plusHours(5)),
            //A flight departing in the past (Рейс, вылетающий в прошлое)
            createFlight(threeDaysFromNow.minusDays(6), threeDaysFromNow),
            //A flight that departs before it arrives (Рейс, который вылетает раньше, чем прибывает)
            createFlight(threeDaysFromNow, threeDaysFromNow.minusHours(6)),
            //A flight with more than two hours ground time (Полет с более чем двумя часами наземного времени)
            createFlight(threeDaysFromNow, threeDaysFromNow.plusHours(2),
                    threeDaysFromNow.plusHours(5), threeDaysFromNow.plusHours(6)),
            //Another flight with more than two hours ground time (Еще один полет с более чем двумя часами наземного времени)
            createFlight(threeDaysFromNow, threeDaysFromNow.plusHours(2),
                threeDaysFromNow.plusHours(3), threeDaysFromNow.plusHours(4),
                threeDaysFromNow.plusHours(6), threeDaysFromNow.plusHours(7)));
    }

    private static Flight createFlight(final LocalDateTime... dates) {
        if ((dates.length % 2) != 0) {
            throw new IllegalArgumentException(
                "you must pass an even number of dates");  //вы должны пройти четное число дат
        }
        List<Segment> segments = new ArrayList<>(dates.length / 2);
        for (int i = 0; i < (dates.length - 1); i += 2) {
            segments.add(new Segment(dates[i], dates[i + 1]));
        }
        return new Flight(segments);
    }
}

/**
 * Bean that represents a flight (Боб, который представляет собой полет).
 */
class Flight {
    private final List<Segment> segments;

    Flight(final List<Segment> segs) {
        segments = segs;
    }

    List<Segment> getSegments() {
        return segments;
    }

    @Override
    public String toString() {
        return segments.stream().map(Object::toString)
            .collect(Collectors.joining(" "));
    }
}

/**
 * Bean that represents a flight segment (Боб, который представляет собой сегмент полета).
 */
class Segment {
    private final LocalDateTime departureDate; // дата отправления

    private final LocalDateTime arrivalDate; // дата прибытия

    Segment(final LocalDateTime dep, final LocalDateTime arr) {
        departureDate = Objects.requireNonNull(dep);
        arrivalDate = Objects.requireNonNull(arr);
    }

    LocalDateTime getDepartureDate() {
        return departureDate;
    }

    LocalDateTime getArrivalDate() {
        return arrivalDate;
    }

    @Override
    public String toString() {
        DateTimeFormatter fmt =
                DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm"); //2007-12-03T10:15:30, т.е. год-месяц-день-час-минута-секунда или 2007-12-03'T'10:15
        return '[' + departureDate.format(fmt) + '|' + arrivalDate.format(fmt)
                + ']';
    }

    public static void main(String[] args) {

        System.out.println("Тестовый набор полетов: ");
        System.out.println(FlightBuilder.createFlights());
        System.out.println();
        ArrayList<Flight> sort = new ArrayList<Flight>(FlightBuilder.createFlights());
        for (int i = 0; i < sort.size(); i++) {
            Flight b = sort.get(i);
            int c = b.getSegments().size();
            if (c >= 0) {
                for (int n = 0; n < c; n++) {
                    long raznica = ChronoUnit.HOURS.between(LocalDateTime.now(), b.getSegments().get(n).departureDate);
                    if (raznica < 0) {
                        sort.remove(i);
                        i--;
                    }
                }
            }
        }
        System.out.println("Список рейсов тестового набора, из которого исключены рейсы с вылетом до текущего момента времени: ");
        System.out.println(sort);
        System.out.println();

        System.out.println("Тестовый набор полетов: ");
        System.out.println(FlightBuilder.createFlights());
        System.out.println();
        ArrayList<Flight> sort2 = new ArrayList<Flight>(FlightBuilder.createFlights());
        for (int i = 0; i < sort2.size(); i++) {
            Flight b = sort2.get(i);
            int c = b.getSegments().size();
            if (c >= 0) {
                for (int n = 0; n < c; n++) {
                    long raznica = ChronoUnit.HOURS.between(b.getSegments().get(n).arrivalDate, b.getSegments().get(n).departureDate);
                    if (raznica > 0) {
                        sort2.remove(i);
                        i--;
                    }
                }
            }
        }
        System.out.println("Список рейсов тестового набора, из которого исключены рейсы с временем прибытия раньше времени вылета: ");
        System.out.println(sort2);
        System.out.println();

        System.out.println("Тестовый набор полетов: ");
        System.out.println(FlightBuilder.createFlights());
        System.out.println();
        ArrayList<Flight> sort3 = new ArrayList<Flight>(FlightBuilder.createFlights());
        for (int i = 0; i < sort3.size(); i++) {
            Flight b = sort3.get(i);
            int c = b.getSegments().size();
            if (c > 0) {
                for (int n = 0; n < (c - 1); n++) {
                    long raznica = ChronoUnit.HOURS.between(b.getSegments().get(n).getArrivalDate(), b.getSegments().get(n + 1).getDepartureDate());
                    if (raznica > 2) {
                        sort3.remove(i);
                        i--;
                    }
                }
            }
        }
            System.out.println("Список рейсов тестового набора, из которого исключены рейсы, у которых общее время проведённое на земле превышает два часа:  ");
            System.out.println(sort3);
            System.out.println();

    }

}