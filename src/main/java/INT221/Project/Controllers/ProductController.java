package INT221.Project.Controllers;

import INT221.Project.Exceptions.ResourceAlreadyExists;
import INT221.Project.Exceptions.ResourceNotFoundException;
import INT221.Project.Models.Products;
import INT221.Project.Services.FileStorageService;
import INT221.Project.Services.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@CrossOrigin(origins = {"http://localhost:8081"})
@RestController
public class ProductController {

    @Autowired
    ProductService productService;

    @Autowired
    FileStorageService fileStorageService;

    @GetMapping("/product")
    public List<Products> showAllProducts(){
        return productService.allProducts();
    }

    @GetMapping("/product/{productId}")
    public Products showProduct(@PathVariable Integer productId){
        if(productService.showProduct(productId) == null ) {
            throw new ResourceNotFoundException("ProductId:"+productId+" is not found.");
        }
        return productService.showProduct(productId);
    }

    @Transactional
    @DeleteMapping("/product/delete/{productId}")
    public void deleteProduct(@PathVariable Integer productId) {
        productService.deleteProduct(productId);
    }

    @PutMapping("/product/edit/{productId}")
    public Optional<Products> updateProduct(@RequestParam("file") MultipartFile file, @PathVariable Integer productId, @RequestPart Products newProduct) {
        fileStorageService.delete(productService.showProduct(productId).getProductImg());
        fileStorageService.save(file);
        return productService.updateProduct(productId, newProduct);
    }

    @Transactional
    @PostMapping("/add")
    public Products newProduct(@RequestParam("file")MultipartFile file,@RequestPart Products newProduct){
        if(!productService.findProduct(newProduct.getProductName()).isEmpty()) {
            throw new ResourceAlreadyExists("ProductName:"+newProduct.getProductName()+" is already exist.");
        }
        fileStorageService.save(file);
        return productService.addProduct(newProduct);
    }

    @GetMapping("/image/{filename:.+}")
    public ResponseEntity<Resource> getFile(@PathVariable String filename) {
        Resource file = fileStorageService.load(filename);
        return ResponseEntity.ok().contentType(MediaType.IMAGE_PNG).header(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; fliename=\"" + file.getFilename()+"\"").body(file);
    }









}