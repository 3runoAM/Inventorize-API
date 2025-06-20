package edu.infnet.inventorize.loaders;

import edu.infnet.inventorize.entities.AuthUser;
import edu.infnet.inventorize.entities.Inventory;
import edu.infnet.inventorize.entities.Item;
import edu.infnet.inventorize.entities.Product;
import edu.infnet.inventorize.enums.Role;
import edu.infnet.inventorize.repository.AuthUserRepository;
import edu.infnet.inventorize.repository.InventoryRepository;
import edu.infnet.inventorize.repository.ItemRepository;
import edu.infnet.inventorize.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.Set;

@Component
@Profile("dev")
@RequiredArgsConstructor
public class DataLoader implements ApplicationRunner {
    private final List<AuthUser> allUsers;
    private final List<Inventory> allInventories;

    private final AuthUserRepository userRepository;
    private final InventoryRepository inventoryRepository;
    private final ProductRepository productRepository;
    private final ItemRepository itemRepository;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        loadUsers();
        loadInventories();
        loadProducts();
        loadItems();
    }

    private void loadUsers() throws IOException {
        var lines = Files.readAllLines(new ClassPathResource("static/csv/users_data.csv").getFile().toPath());
        var encriptor = new BCryptPasswordEncoder();

        for (var line : lines) {
            var data = line.split(",");

            var user = AuthUser.builder()
                    .email(data[0])
                    .hashPassword(encriptor.encode(data[1]))
                    .roles(Set.of(Role.ROLE_USER))
                    .build();

            var savedUser = userRepository.save(user);
            System.out.println("Usuário salvo com o email: " + savedUser.getEmail() + " e com o ID: " + savedUser.getId());
            allUsers.add(savedUser);
        }
    }

    private void loadInventories() throws IOException {
        var lines = Files.readAllLines(new ClassPathResource("static/csv/inventories_data.csv").getFile().toPath());

        for (int i = 0; i < allUsers.size(); i++) {
            var user = allUsers.get(i);
            var data = lines.get(i).split(",");

            var inventory = Inventory.builder()
                    .name(data[0])
                    .description(data[1])
                    .notificationEmail(data[2])
                    .owner(user)
                    .build();

            var savedInventory = inventoryRepository.save(inventory);
            System.out.println("Inventário salvo com o nome: " + savedInventory.getName() + " para o usuário: " + user.getEmail());
            allInventories.add(savedInventory);
        }
    }

    private void loadProducts() throws IOException {
        var productsData01 = Files.readAllLines(new ClassPathResource("static/csv/products_data_01").getFile().toPath());
        var productsData02 = Files.readAllLines(new ClassPathResource("static/csv/products_data_02").getFile().toPath());
        var productsData03 = Files.readAllLines(new ClassPathResource("static/csv/products_data_03").getFile().toPath());
        var productsData04 = Files.readAllLines(new ClassPathResource("static/csv/products_data_04").getFile().toPath());
        var productsData05 = Files.readAllLines(new ClassPathResource("static/csv/products_data_05").getFile().toPath());

        loadProductsPerUser(productsData01);
        loadProductsPerUser(productsData02);
        loadProductsPerUser(productsData03);
        loadProductsPerUser(productsData04);
        loadProductsPerUser(productsData05);
    }

    private void loadProductsPerUser(List<String> fileLines) throws IOException {
        for (int i = 0; i < allUsers.size(); i++) {
            var inventory = allInventories.get(i);
            var data = fileLines.get(i).split(",");

            var product = Product.builder()
                    .name(data[0])
                    .supplierCode(data[1])
                    .owner(allUsers.get(i))
                    .build();

            productRepository.save(product);
            System.out.println("Produto salvo com o nome: " + product.getName() + " para o inventário: " + inventory.getName());
        }
    }

    private void loadItems() throws IOException {
        for (var user : allUsers) {
            var userProducts = productRepository.findAllByOwnerId(user.getId());
            var userInventory = inventoryRepository.findByOwnerId(user.getId()).get(0);

            for (var product : userProducts) {
                var item = Item.builder()
                        .product(product)
                        .inventory(userInventory)
                        .currentQuantity(10)
                        .minimumStockLevel(5)
                        .build();

                var savedItem = itemRepository.save(item);
                System.out.println("Item salvo com o ID: " + savedItem.getId() +
                                   ", produto: " + product.getName() +
                                   ", inventário: " + userInventory.getName() +
                                   ", quantidade atual: " + savedItem.getCurrentQuantity() +
                                   ", nível mínimo de estoque: " + savedItem.getMinimumStockLevel());
            }
        }
    }
}