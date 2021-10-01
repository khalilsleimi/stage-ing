package com.vneuron.vneuroncheeseservice.bootstrap;

import com.vneuron.creamery.model.CheeseStyleEnum;
import com.vneuron.vneuroncheeseservice.domain.Cheese;
import com.vneuron.vneuroncheeseservice.repositories.CheeseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;


@RequiredArgsConstructor
@Component
public class CheeseLoader implements CommandLineRunner {

    public static final String CHEESE_1_UPC = "0631234200036";
    public static final String CHEESE_2_UPC = "0631234300019";
    public static final String CHEESE_3_UPC = "0083783375213";

    private final CheeseRepository cheeseRepository;

    @Override
    public void run(String... args) throws Exception {

          if(cheeseRepository.count() == 0 ) {
              loadCheeseObjects();
          }
    }

    private void loadCheeseObjects() {
        Cheese b1 = Cheese.builder()
                .cheeseName("Mango Bobs")
                .cheeseStyle(CheeseStyleEnum.IPA.name())
                .minOnHand(12)
                .quantityToRipen(200)
                .price(new BigDecimal("12.95"))
                .upc(CHEESE_1_UPC)
                .build();

        Cheese b2 = Cheese.builder()
                .cheeseName("Galaxy Cat")
                .cheeseStyle(CheeseStyleEnum.PALE_ALE.name())
                .minOnHand(12)
                .quantityToRipen(200)
                .price(new BigDecimal("12.95"))
                .upc(CHEESE_2_UPC)
                .build();

        Cheese b3 = Cheese.builder()
                .cheeseName("Pinball Porter")
                .cheeseStyle(CheeseStyleEnum.PALE_ALE.name())
                .minOnHand(12)
                .quantityToRipen(200)
                .price(new BigDecimal("12.95"))
                .upc(CHEESE_3_UPC)
                .build();

        cheeseRepository.save(b1);
        cheeseRepository.save(b2);
        cheeseRepository.save(b3);
    }
}
