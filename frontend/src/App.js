import React, { useState, useEffect } from 'react';
import axios from 'axios';
import './App.css';

const API = "http://localhost:8080/api";

function App() {
  const [token, setToken] = useState(localStorage.getItem("token"));
  const [view, setView] = useState("products");
  const [products, setProducts] = useState([]);

  // Cart State (Persisted in LocalStorage)
  const [cart, setCart] = useState(() => {
    const saved = localStorage.getItem("cart");
    return saved ? JSON.parse(saved) : {};
  });

  const [page, setPage] = useState(0);
  const [search, setSearch] = useState("");
  const [myOrders, setMyOrders] = useState([]);
  const [auth, setAuth] = useState({ email: "", password: "" });

  // NEW: Loading State
  const [loading, setLoading] = useState(false);

  // Axios Instance (Attaches Token automatically)
  const api = axios.create({
    baseURL: API,
    headers: { Authorization: `Bearer ${token}` }
  });

  // Effects
  useEffect(() => { localStorage.setItem("cart", JSON.stringify(cart)); }, [cart]);
  useEffect(() => { fetchProducts(); }, [page, search]);
  useEffect(() => { if(token && view === 'orders') fetchOrders(); }, [view, token]);

  const handleAuth = async (endpoint) => {
    setLoading(true);
    try {
      const res = await axios.post(`${API}/auth/${endpoint}`, auth);
      localStorage.setItem("token", res.data.token);
      setToken(res.data.token);
    } catch (e) { alert("Auth Failed"); }
    setLoading(false);
  };

  const fetchProducts = async () => {
    setLoading(true);
    try {
      const endpoint = search ? `/products/search?query=${search}&page=${page}` : `/products?page=${page}`;
      const res = await axios.get(`${API}${endpoint}`);
      setProducts(res.data.content);
    } catch (e) { console.error(e); }
    setLoading(false);
  };

  const fetchOrders = async () => {
    try {
      const res = await api.get("/orders");
      setMyOrders(res.data);
    } catch (e) { console.error(e); }
  };

  const addToCart = (p) => {
    const currentQty = cart[p.id]?.qty || 0;

    // NEW: Validation to prevent adding more than available stock
    if (currentQty >= p.stock) {
      alert(`Only ${p.stock} items available!`);
      return;
    }

    setCart(prev => ({
      ...prev,
      [p.id]: { ...p, qty: currentQty + 1 }
    }));
  };

  // NEW: Remove from Cart Function
  const removeFromCart = (id) => {
    setCart(prev => {
      const newCart = { ...prev };
      delete newCart[id];
      return newCart;
    });
  };

  const placeOrder = async () => {
    setLoading(true);
    const items = Object.values(cart).map(i => ({ productId: i.id, quantity: i.qty }));
    try {
      await api.post("/orders", { items });
      alert("Order Successful!");
      setCart({});
      setView("orders");
    } catch (err) {
      alert("Error: " + (err.response?.data || "Failed"));
    }
    setLoading(false);
  };

  // --- UI RENDER ---
  if (!token) return (
      <div className="container" style={{maxWidth: '400px', margin: '50px auto', textAlign:'center'}}>
        <h1>Nexus Gear Login</h1>
        <div style={{display:'flex', flexDirection:'column', gap:'10px'}}>
          <input placeholder="Email" onChange={e => setAuth({...auth, email: e.target.value})} />
          <input type="password" placeholder="Password" onChange={e => setAuth({...auth, password: e.target.value})} />
          <button disabled={loading} onClick={() => handleAuth("login")}>
            {loading ? "Processing..." : "Login"}
          </button>
          <button disabled={loading} onClick={() => handleAuth("register")}>Register</button>
        </div>
      </div>
  );

  return (
      <div className="app">
        <nav style={{background: '#222', color:'#fff', padding:'1rem', display:'flex', justifyContent:'space-between', alignItems:'center'}}>
          <h2>Nexus Gear</h2>
          <div style={{gap:'10px', display:'flex'}}>
            <button onClick={() => setView("products")}>Shop</button>
            <button onClick={() => setView("cart")}>Cart ({Object.values(cart).reduce((a,b)=>a+b.qty,0)})</button>
            <button onClick={() => setView("orders")}>My Orders</button>
            <button onClick={() => { localStorage.removeItem("token"); setToken(null); }}>Logout</button>
          </div>
        </nav>

        <div style={{padding:'20px'}}>
          {loading && <p style={{textAlign:'center', color:'blue'}}>Loading data...</p>}

          {view === "products" && (
              <div>
                <input placeholder="Search products..." onChange={e => setSearch(e.target.value)} style={{padding:'10px', width:'100%', marginBottom:'20px'}} />
                <div style={{display:'grid', gridTemplateColumns:'repeat(auto-fill, minmax(200px, 1fr))', gap:'20px'}}>
                  {products.map(p => (
                      <div key={p.id} style={{border:'1px solid #ddd', padding:'10px', borderRadius:'8px', background:'white'}}>
                        {/* NEW: Image Fallback */}
                        <img
                            src={p.imageUrl}
                            alt={p.name}
                            style={{width:'100%', height:'150px', objectFit:'cover', borderRadius:'4px'}}
                            onError={(e) => e.target.src = 'https://placehold.co/300?text=No+Image'}
                        />
                        <h3>{p.name}</h3>
                        <p style={{color:'#666', fontSize:'0.9rem'}}>{p.description}</p>
                        <div style={{display:'flex', justifyContent:'space-between', alignItems:'center', marginTop:'10px'}}>
                          <span style={{fontWeight:'bold', fontSize:'1.1rem'}}>${p.price}</span>
                          <span style={{color: p.stock<5?'red':'green', fontSize:'0.9rem'}}>{p.stock} left</span>
                        </div>
                        <button
                            disabled={p.stock < 1}
                            onClick={() => addToCart(p)}
                            style={{width:'100%', marginTop:'10px', padding:'10px', background: p.stock>0?'black':'grey', color:'white', border:'none', cursor:'pointer'}}
                        >
                          {p.stock > 0 ? "Add to Cart" : "Out of Stock"}
                        </button>
                      </div>
                  ))}
                </div>
                <div style={{marginTop:'20px', display:'flex', justifyContent:'center', gap:'10px'}}>
                  <button disabled={page===0} onClick={() => setPage(page-1)}>Previous</button>
                  <button onClick={() => setPage(page+1)}>Next</button>
                </div>
              </div>
          )}

          {view === "cart" && (
              <div style={{maxWidth:'600px', margin:'0 auto'}}>
                <h2>Your Shopping Cart</h2>
                {Object.values(cart).map(item => (
                    <div key={item.id} style={{display:'flex', justifyContent:'space-between', alignItems:'center', borderBottom:'1px solid #eee', padding:'15px 0'}}>
                      <div style={{display:'flex', gap:'15px', alignItems:'center'}}>
                        <img src={item.imageUrl} alt={item.name} style={{width:'50px', height:'50px', borderRadius:'4px'}} onError={(e) => e.target.src = 'https://placehold.co/50'} />
                        <div>
                          <h4>{item.name}</h4>
                          <p>${item.price} x {item.qty}</p>
                        </div>
                      </div>
                      <div style={{display:'flex', alignItems:'center', gap:'15px'}}>
                        <span style={{fontWeight:'bold'}}>${(item.price * item.qty).toFixed(2)}</span>
                        {/* NEW: Remove Button */}
                        <button onClick={() => removeFromCart(item.id)} style={{background:'red', color:'white', border:'none', padding:'5px 10px', cursor:'pointer'}}>X</button>
                      </div>
                    </div>
                ))}
                {Object.keys(cart).length > 0 ? (
                    <button
                        onClick={placeOrder}
                        disabled={loading}
                        style={{width:'100%', marginTop:'20px', padding:'15px', background:'green', color:'white', border:'none', fontSize:'1.1rem', cursor:'pointer'}}
                    >
                      {loading ? "Processing Order..." : `Checkout ($${Object.values(cart).reduce((sum, i) => sum + (i.price * i.qty), 0).toFixed(2)})`}
                    </button>
                ) : <p style={{textAlign:'center'}}>Your cart is empty.</p>}
              </div>
          )}

          {view === "orders" && (
              <div style={{maxWidth:'800px', margin:'0 auto'}}>
                <h2>Order History</h2>
                {myOrders.length === 0 && <p>No orders yet.</p>}
                {myOrders.map(o => (
                    <div key={o.id} style={{border:'1px solid #ccc', padding:'15px', margin:'10px 0', borderRadius:'8px', background:'white'}}>
                      <div style={{display:'flex', justifyContent:'space-between'}}>
                        <strong>Order #{o.id}</strong>
                        <span style={{color:'#666'}}>{new Date(o.date).toLocaleString()}</span>
                      </div>
                      <div style={{marginTop:'10px', display:'flex', justifyContent:'space-between'}}>
                        <span style={{background:'#e0ffe0', padding:'2px 8px', borderRadius:'4px', color:'green'}}>{o.status}</span>
                        <strong style={{fontSize:'1.2rem'}}>${o.total}</strong>
                      </div>
                    </div>
                ))}
              </div>
          )}
        </div>
      </div>
  );
}

export default App;