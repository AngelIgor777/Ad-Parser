package com.parser.parser_999md.util;

import com.parser.parser_999md.Entity.Car;
import com.parser.parser_999md.repository.CarRepository;
import lombok.RequiredArgsConstructor;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JsoupDocumentParser {

    private final CarRepository carRepository;

    @Scheduled(cron = "${cron.scheduler}")
    public void addCarsInDatabase() {
        // URL документа, который вы хотите парсить
        String url = "https://999.md/ru/list/transport/cars?hide_duplicates=yes&sort_type=date_desc&applied=1&sort_expanded=yes&ef=260,1,6,7,5,4,1279,1081,3,4112,2029&r_6_2_from=&r_6_2_to=&r_6_2_unit=eur&r_6_2_negotiable=yes&r_7_19_from=&r_7_19_to=&r_1081_104_from=&r_1081_104_to=&r_1081_104_unit=km";

        try {
            // Загружаем документ с указанного URL
            Document document = Jsoup.connect(url).get();

            // Получаем все элементы с классом "ads-list-photo-item"
            Elements carElements = document.select("li.ads-list-photo-item");

            // Список для хранения всех найденных объектов Car
            List<Car> cars = new ArrayList<>();

            for (Element carElement : carElements) {
                // Извлекаем данные
                String carTitle = carElement.select(".ads-list-photo-item-title a").text();
                String photoUrl = carElement.select(".ads-list-photo-item-thumb img").attr("src");
                String mileage = carElement.select(".is-offer-type span").text();
                String carUrl = carElement.select(".ads-list-photo-item-title a").attr("href");

                if (!carUrl.isEmpty()) {
                    carUrl = "https://999.md" + carUrl;

                    if (mileage.isEmpty()) {
                        mileage = "0";
                    }
                    Car car = Car.builder()
                            .carTitle(carTitle)
                            .photoUrl(photoUrl)
                            .mileage(mileage)
                            .carUrl(carUrl).build();

                    if (!carRepository.existsByCarUrl(carUrl)) {
                        cars.add(car);
                    }
                }

            }


            carRepository.saveAll(cars);


        } catch (IOException e) {
            System.err.println("Error fetching or parsing the document: " + e.getMessage());
        }
    }


}
