package com.project.hardwarehub.config;

import com.project.hardwarehub.entity.Category;
import com.project.hardwarehub.repository.CategoryRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner initCategories(CategoryRepository categoryRepository) {
        return args -> {
            List<Category> defaultCategories = List.of(
                    new Category("MEMORY", "DRAM, NAND, EEPROM, and high-speed Flash memory"),
                    new Category("CONNECTORS", "USB-C, PCIe, headers, terminal blocks, and ribbon cables"),
                    new Category("MICROCONTROLLERS", "ARM Cortex, ESP32, STM32, RISC-V, and 8-bit MCUs"),
                    new Category("POWER MODULES", "Buck converters, boost modules, LDOs, and PMICs"),
                    new Category("SENSORS", "IMUs, environmental sensors, optical detectors, and hall sensors")
            );

            for (Category cat : defaultCategories) {
                if (!categoryRepository.existsByNameIgnoreCase(cat.getName())) {
                    categoryRepository.save(cat);
                }
            }
        };
    }
}