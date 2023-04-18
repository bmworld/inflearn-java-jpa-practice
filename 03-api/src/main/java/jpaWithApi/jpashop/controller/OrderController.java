package jpaWithApi.jpashop.controller;

import jpaWithApi.jpashop.domain.item.Item;
import jpaWithApi.jpashop.domain.member.Member;
import jpaWithApi.jpashop.domain.order.Order;
import jpaWithApi.jpashop.repository.OrderSearch;
import jpaWithApi.jpashop.service.ItemService;
import jpaWithApi.jpashop.service.MemberService;
import jpaWithApi.jpashop.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class OrderController {

  private final OrderService orderService;
  private final MemberService memberService;
  private final ItemService itemService;

  @GetMapping("/order")
  public String createForm(Model model) {
    List<Member> members = memberService.findMembers();
    List<Item> items = itemService.findItems();

    model.addAttribute("members", members);
    model.addAttribute("items", items);

    return "order/orderForm";
  }

  @PostMapping("/order")
  public String order(
      @RequestParam("memberId") Long memberId,
      @RequestParam("itemId") Long itemId,
      @RequestParam("count") int count
  ) {
    orderService.order(memberId, itemId, count);

    return "redirect:/orders";
  }


  @GetMapping("/orders")
  public String orderList(@ModelAttribute("orderSearch") OrderSearch orderSearch, Model model) {

    // 김영한 강사님: 단순 조회일 경우, Controller에서 Repository 접근하는 방식 사용하심

    List<Order> orders = orderService.findOrders(orderSearch);

    model.addAttribute("orders", orders);


    return "order/orderList";
  }

  @PostMapping("/orders/{orderId}/cancel")
  public String cancelOrder(@PathVariable("orderId") Long orderId) {

    orderService.cancelOrder(orderId);

    return "redirect:/orders";
  }

}
