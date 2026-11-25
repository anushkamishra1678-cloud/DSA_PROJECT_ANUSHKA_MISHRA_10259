

// Product Catalog
const products = [
    {id: "P101", name: "Notebook", price: 50, icon: "📓"},
    {id: "P102", name: "Pen", price: 10, icon: "🖊️"},
    {id: "P103", name: "Bag", price: 800, icon: "🎒"}
];

// Shopping Cart and Stack
let cart = [];
let actionStack = [];

// Display Products
function displayProducts() {

    let html = "";

    products.forEach(product => {

        html += `
        <div class="product">

            <div class="icon">${product.icon}</div>

            <h3>${product.name}</h3>

            <p>Product ID: ${product.id}</p>

            <p class="price">₹${product.price}</p>

            <input type="number"
                   id="qty-${product.id}"
                   value="1"
                   min="1">

            <button onclick="addToCart('${product.id}')">
                Add to Cart
            </button>

        </div>
        `;
    });

    document.getElementById("productList").innerHTML = html;
}

// Add Product to Cart
function addToCart(id) {

    let product = products.find(p => p.id === id);

    let quantity = Number(
        document.getElementById("qty-" + id).value
    );

    if (!Number.isInteger(quantity) || quantity <= 0) {
        alert("Enter a valid positive quantity.");
        return;
    }

    let existing = cart.find(item => item.id === id);

    if (existing) {
        existing.quantity += quantity;
    } else {
        cart.push({
            ...product,
            quantity: quantity
        });
    }

    // Push Action into Stack
    actionStack.push({
        id: id,
        quantity: quantity
    });

    alert(product.name + " added to cart!");

    displayCart();
}

// Display Shopping Cart
function displayCart() {

    let cartHTML = "";
    let grandTotal = 0;
    let count = 0;

    if (cart.length === 0) {

        cartHTML =
        '<p class="empty">Your shopping cart is empty.</p>';

    } else {

        cart.forEach((item, index) => {

            let itemTotal = item.price * item.quantity;

            grandTotal += itemTotal;
            count += item.quantity;

            cartHTML += `
            <div class="cart-item">

                <div>
                    <strong>${index + 1}. ${item.name}</strong>
                    <p>Quantity: ${item.quantity}</p>
                    <small>₹${item.price} × ${item.quantity}</small>
                </div>

                <strong>₹${itemTotal}</strong>

            </div>
            `;
        });
    }

    document.getElementById("cartItems").innerHTML = cartHTML;

    document.getElementById("total").innerText = grandTotal;

    document.getElementById("navCount").innerText = count;
}

// Remove Last Added Item (Stack Pop)
function undoLast() {

    if (actionStack.length === 0) {
        alert("Cart is empty. Nothing to remove.");
        return;
    }

    let lastAction = actionStack.pop();

    let item = cart.find(
        p => p.id === lastAction.id
    );

    if (item) {

        item.quantity -= lastAction.quantity;

        if (item.quantity <= 0) {
            cart = cart.filter(
                p => p.id !== lastAction.id
            );
        }
    }

    alert("Last added item removed.");

    displayCart();
}

// Sort Cart (High to Low)
function sortCart() {

    if (cart.length === 0) {
        alert("Your cart is empty.");
        return;
    }

    cart.sort((a, b) =>
        (b.price * b.quantity) -
        (a.price * a.quantity)
    );

    displayCart();

    alert("Cart sorted successfully!");
}

// Checkout
function checkout() {

    if (cart.length === 0) {
        alert("Your cart is empty.");
        return;
    }

    let total = cart.reduce(
        (sum, item) =>
        sum + item.price * item.quantity, 0
    );

    alert(
        "Order placed successfully!\n" +
        "Total Amount: ₹" + total
    );

    cart = [];
    actionStack = [];

    displayCart();
}

// Initialize Website
displayProducts();
displayCart();