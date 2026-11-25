package tacos;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import tacos.data.IngredientRepository;
import tacos.data.OrderRepository;

@WebMvcTest
public class CartControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockitoBean
  private IngredientRepository ingredientRepository;

  @MockitoBean
  private OrderRepository orderRepository;

  private TacoOrder orderWithTacos(String... names) {
    TacoOrder order = new TacoOrder();
    for (String name : names) {
      Taco taco = new Taco();
      taco.setName(name);
      order.addTaco(taco);
    }
    return order;
  }

  @Test
  public void removeTacoTakesItOutOfTheCart() throws Exception {
    TacoOrder order = orderWithTacos("First", "Second", "Third");

    mockMvc.perform(post("/cart/remove")
            .param("index", "1")
            .sessionAttr("tacoOrder", order))
        .andExpect(redirectedUrl("/cart"));

    assertThat(order.getTacos())
        .extracting(Taco::getName)
        .containsExactly("First", "Third");
  }

  @Test
  public void removeTacoWithInvalidIndexChangesNothing() throws Exception {
    TacoOrder order = orderWithTacos("Only");

    mockMvc.perform(post("/cart/remove")
            .param("index", "5")
            .sessionAttr("tacoOrder", order))
        .andExpect(redirectedUrl("/cart"));

    assertThat(order.getTacos()).hasSize(1);
  }

}
