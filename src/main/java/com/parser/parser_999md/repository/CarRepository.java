package com.parser.parser_999md.repository;

import com.parser.parser_999md.Entity.Car;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CarRepository extends JpaRepository<Car, Integer> {
    boolean existsByCarUrl(String carUrl);
}
