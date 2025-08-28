import React, { useState, useEffect } from 'react';
import {
  Send,
  Star,
  Trophy,
  Gift,
  ArrowRight,
  History,
  Award,
  ShoppingCart,
  Plus,
  Minus,
  Check,
  Coins
} from 'lucide-react';

const App = () => {
  const [activeTab, setActiveTab] = useState('send');
  const [points, setPoints] = useState(1250);
  const [tier, setTier] = useState('Silver');
  const [sendAmount, setSendAmount] = useState('');
  const [recipient, setRecipient] = useState('');
  const [isAnimating, setIsAnimating] = useState(false);
  const [cart, setCart] = useState([]);
  const [showSuccess, setShowSuccess] = useState(false);

  const [transactions, setTransactions] = useState([
    { id: 1, amount: 500, points: 5, date: '2025-08-25', recipient: 'John Doe', country: 'Zimbabwe' },
    { id: 2, amount: 1000, points: 10, date: '2025-08-20', recipient: 'Mary Smith', country: 'Malawi' },
    { id: 3, amount: 750, points: 7, date: '2025-08-15', recipient: 'David Wilson', country: 'Kenya' }
  ]);

  const rewards = [
    { id: 1, name: 'Airtime R50', cost: 500, category: 'Mobile', icon: '📱', description: 'Mobile airtime voucher' },
    { id: 2, name: 'Data Bundle 5GB', cost: 800, category: 'Mobile', icon: '📶', description: '5GB data bundle' },
    { id: 3, name: 'Grocery Voucher R100', cost: 1000, category: 'Shopping', icon: '🛒', description: 'Grocery store voucher' },
    { id: 4, name: 'Coffee Shop Voucher', cost: 300, category: 'Food', icon: '☕', description: 'Coffee shop voucher' },
    { id: 5, name: 'Movie Ticket', cost: 600, category: 'Entertainment', icon: '🎬', description: 'Cinema movie ticket' },
    { id: 6, name: 'Digital Badge: Explorer', cost: 100, category: 'Digital', icon: '🏆', description: 'Special achievement badge' }
  ];

  const handleSendMoney = () => {
    if (!sendAmount || !recipient) return;

    setIsAnimating(true);

    setTimeout(() => {
      const amount = parseFloat(sendAmount);
      const earnedPoints = Math.floor(amount / 100);
      const newTransaction = {
        id: transactions.length + 1,
        amount: amount,
        points: earnedPoints,
        date: new Date().toISOString().split('T')[0],
        recipient: recipient,
        country: 'South Africa'
      };

      setTransactions([newTransaction, ...transactions]);
      setPoints(points + earnedPoints);
      setSendAmount('');
      setRecipient('');
      setIsAnimating(false);
      setShowSuccess(true);

      setTimeout(() => setShowSuccess(false), 3000);
    }, 2000);
  };

  const addToCart = (reward) => {
    const existingItem = cart.find(item => item.id === reward.id);
    if (existingItem) {
      setCart(cart.map(item =>
        item.id === reward.id
          ? { ...item, quantity: item.quantity + 1 }
          : item
      ));
    } else {
      setCart([...cart, { ...reward, quantity: 1 }]);
    }
  };

  const removeFromCart = (rewardId) => {
    setCart(cart.filter(item => item.id !== rewardId));
  };

  const updateCartQuantity = (rewardId, change) => {
    setCart(cart.map(item => {
      if (item.id === rewardId) {
        const newQuantity = item.quantity + change;
        return newQuantity > 0 ? { ...item, quantity: newQuantity } : null;
      }
      return item;
    }).filter(Boolean));
  };

  const getTotalCartCost = () => {
    return cart.reduce((total, item) => total + (item.cost * item.quantity), 0);
  };

  const checkout = () => {
    const totalCost = getTotalCartCost();
    if (totalCost <= points) {
      setPoints(points - totalCost);
      setCart([]);
      setShowSuccess(true);
      setTimeout(() => setShowSuccess(false), 3000);
    }
  };

  const getTierProgress = () => {
    const tiers = { Bronze: 500, Silver: 1500, Gold: 3000 };
    const currentTierMax = tiers[tier];
    const progress = (points / currentTierMax) * 100;
    return Math.min(progress, 100);
  };

  return (
    <div className="min-h-screen bg-gray-900 text-white">
      {/* Header */}
      <div className="bg-gray-800 border-b border-gray-700">
        <div className="max-w-4xl mx-auto px-4 py-4">
          <div className="flex items-center justify-between">
            <div className="flex items-center space-x-3">
              <div className="w-10 h-10 bg-orange-500 rounded-lg flex items-center justify-center">
                <Star className="w-6 h-6 text-white" />
              </div>
              <div>
                <h1 className="text-xl font-bold text-orange-400">Mukuru</h1>
                <p className="text-sm text-gray-400">Loyalty Hub</p>
              </div>
            </div>
            <div className="text-right">
              <div className="flex items-center space-x-2">
                <Coins className="w-5 h-5 text-orange-400" />
                <span className="text-2xl font-bold text-orange-400">{points.toLocaleString()}</span>
              </div>
              <div className="flex items-center space-x-1">
                <Trophy className="w-4 h-4 text-yellow-400" />
                <span className="text-sm text-yellow-400">{tier} Tier</span>
              </div>
            </div>
          </div>
        </div>
      </div>

      {/* Success Notification */}
      {showSuccess && (
        <div className="fixed top-20 right-4 bg-green-600 text-white px-6 py-3 rounded-lg shadow-lg z-50 flex items-center space-x-2">
          <Check className="w-5 h-5" />
          <span>Success! Points earned/redeemed</span>
        </div>
      )}

      {/* Tier Progress Bar */}
      <div className="max-w-4xl mx-auto px-4 py-4">
        <div className="bg-gray-800 rounded-lg p-4">
          <div className="flex justify-between items-center mb-2">
            <span className="text-sm text-gray-400">Progress to Gold Tier</span>
            <span className="text-sm text-orange-400">{Math.round(getTierProgress())}%</span>
          </div>
          <div className="w-full bg-gray-700 rounded-full h-2">
            <div
              className="bg-gradient-to-r from-orange-500 to-yellow-400 h-2 rounded-full transition-all duration-500"
              style={{ width: `${getTierProgress()}%` }}
            ></div>
          </div>
        </div>
      </div>

      {/* Navigation Tabs */}
      <div className="max-w-4xl mx-auto px-4">
        <div className="flex space-x-1 bg-gray-800 rounded-lg p-1">
          {[
            { id: 'send', label: 'Send Money', icon: Send },
            { id: 'history', label: 'History', icon: History },
            { id: 'rewards', label: 'Rewards', icon: Gift }
          ].map(tab => (
            <button
              key={tab.id}
              onClick={() => setActiveTab(tab.id)}
              className={`flex-1 flex items-center justify-center space-x-2 py-3 px-4 rounded-md transition-all ${
                activeTab === tab.id 
                  ? 'bg-orange-500 text-white' 
                  : 'text-gray-400 hover:text-white'
              }`}
            >
              <tab.icon className="w-5 h-5" />
              <span className="font-medium">{tab.label}</span>
            </button>
          ))}
        </div>
      </div>

      {/* Content */}
      <div className="max-w-4xl mx-auto px-4 py-6">
        {/* Send Money Tab */}
        {activeTab === 'send' && (
          <div className="space-y-6">
            <div className="bg-gray-800 rounded-lg p-6">
              <h2 className="text-2xl font-bold mb-6 text-orange-400">Send Money & Earn Points</h2>

              <div className="grid md:grid-cols-2 gap-6">
                <div className="space-y-4">
                  <div>
                    <label className="block text-sm font-medium mb-2 text-gray-300">Recipient Name</label>
                    <input
                      type="text"
                      value={recipient}
                      onChange={(e) => setRecipient(e.target.value)}
                      placeholder="Enter recipient name"
                      className="w-full p-3 bg-gray-700 border border-gray-600 rounded-lg focus:ring-2 focus:ring-orange-500 focus:border-transparent"
                    />
                  </div>

                  <div>
                    <label className="block text-sm font-medium mb-2 text-gray-300">Amount (R)</label>
                    <input
                      type="number"
                      value={sendAmount}
                      onChange={(e) => setSendAmount(e.target.value)}
                      placeholder="0.00"
                      className="w-full p-3 bg-gray-700 border border-gray-600 rounded-lg focus:ring-2 focus:ring-orange-500 focus:border-transparent"
                    />
                  </div>

                  {sendAmount && (
                    <div className="bg-gray-700 rounded-lg p-4">
                      <div className="flex justify-between items-center">
                        <span className="text-gray-300">Points to earn:</span>
                        <div className="flex items-center space-x-1">
                          <Star className="w-4 h-4 text-yellow-400" />
                          <span className="text-yellow-400 font-bold">
                            {Math.floor(parseFloat(sendAmount || 0) / 100)}
                          </span>
                        </div>
                      </div>
                    </div>
                  )}

                  <button
                    onClick={handleSendMoney}
                    disabled={!sendAmount || !recipient || isAnimating}
                    className="w-full bg-orange-500 hover:bg-orange-600 disabled:bg-gray-600 text-white font-bold py-3 px-6 rounded-lg transition-all flex items-center justify-center space-x-2"
                  >
                    {isAnimating ? (
                      <>
                        <div className="animate-spin rounded-full h-5 w-5 border-b-2 border-white"></div>
                        <span>Sending...</span>
                      </>
                    ) : (
                      <>
                        <Send className="w-5 h-5" />
                        <span>Send Money</span>
                        <ArrowRight className="w-5 h-5" />
                      </>
                    )}
                  </button>
                </div>

                <div className="bg-gray-700 rounded-lg p-6">
                  <h3 className="text-lg font-bold mb-4 text-orange-400">How Points Work</h3>
                  <div className="space-y-3 text-sm">
                    <div className="flex items-center space-x-2">
                      <Star className="w-4 h-4 text-yellow-400" />
                      <span>Earn 1 point per R100 sent</span>
                    </div>
                    <div className="flex items-center space-x-2">
                      <Trophy className="w-4 h-4 text-yellow-400" />
                      <span>Unlock tiers: Bronze → Silver → Gold</span>
                    </div>
                    <div className="flex items-center space-x-2">
                      <Gift className="w-4 h-4 text-yellow-400" />
                      <span>Redeem points for amazing rewards</span>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        )}

        {/* History Tab */}
        {activeTab === 'history' && (
          <div className="space-y-6">
            <div className="bg-gray-800 rounded-lg p-6">
              <h2 className="text-2xl font-bold mb-6 text-orange-400">Transaction History</h2>

              <div className="space-y-4">
                {transactions.map((transaction) => (
                  <div key={transaction.id} className="bg-gray-700 rounded-lg p-4 flex items-center justify-between">
                    <div className="flex items-center space-x-4">
                      <div className="w-12 h-12 bg-orange-500 rounded-full flex items-center justify-center">
                        <Send className="w-6 h-6 text-white" />
                      </div>
                      <div>
                        <div className="font-medium">{transaction.recipient}</div>
                        <div className="text-sm text-gray-400">{transaction.country} • {transaction.date}</div>
                      </div>
                    </div>
                    <div className="text-right">
                      <div className="font-bold text-white">R{transaction.amount.toLocaleString()}</div>
                      <div className="flex items-center space-x-1 text-yellow-400">
                        <Star className="w-4 h-4" />
                        <span className="text-sm">+{transaction.points}</span>
                      </div>
                    </div>
                  </div>
                ))}
              </div>
            </div>
          </div>
        )}

        {/* Rewards Tab */}
        {activeTab === 'rewards' && (
          <div className="space-y-6">
            <div className="bg-gray-800 rounded-lg p-6">
              <div className="flex justify-between items-center mb-6">
                <h2 className="text-2xl font-bold text-orange-400">Rewards Marketplace</h2>
                {cart.length > 0 && (
                  <div className="flex items-center space-x-2 bg-gray-700 px-4 py-2 rounded-lg">
                    <ShoppingCart className="w-5 h-5 text-orange-400" />
                    <span className="text-orange-400 font-bold">{cart.length}</span>
                  </div>
                )}
              </div>

              <div className="grid sm:grid-cols-2 lg:grid-cols-3 gap-6 mb-8">
                {rewards.map((reward) => (
                  <div key={reward.id} className="bg-gray-700 rounded-lg p-4 hover:bg-gray-600 transition-all">
                    <div className="text-4xl mb-3">{reward.icon}</div>
                    <h3 className="font-bold mb-1">{reward.name}</h3>
                    <p className="text-gray-400 text-sm mb-3">{reward.description}</p>
                    <div className="flex items-center justify-between">
                      <div className="flex items-center space-x-1">
                        <Star className="w-4 h-4 text-yellow-400" />
                        <span className="font-bold text-yellow-400">{reward.cost}</span>
                      </div>
                      <button
                        onClick={() => addToCart(reward)}
                        disabled={points < reward.cost}
                        className="bg-orange-500 hover:bg-orange-600 disabled:bg-gray-600 text-white px-3 py-1 rounded-md text-sm transition-all"
                      >
                        Add to Cart
                      </button>
                    </div>
                  </div>
                ))}
              </div>

              {/* Cart */}
              {cart.length > 0 && (
                <div className="bg-gray-700 rounded-lg p-6">
                  <h3 className="text-xl font-bold mb-4 text-orange-400">Shopping Cart</h3>
                  <div className="space-y-3 mb-4">
                    {cart.map((item) => (
                      <div key={item.id} className="flex items-center justify-between bg-gray-800 p-3 rounded-lg">
                        <div className="flex items-center space-x-3">
                          <span className="text-2xl">{item.icon}</span>
                          <div>
                            <div className="font-medium">{item.name}</div>
                            <div className="text-sm text-gray-400">
                              {item.cost} points each
                            </div>
                          </div>
                        </div>
                        <div className="flex items-center space-x-3">
                          <div className="flex items-center space-x-2">
                            <button
                              onClick={() => updateCartQuantity(item.id, -1)}
                              className="w-8 h-8 bg-gray-600 hover:bg-gray-500 rounded-full flex items-center justify-center"
                            >
                              <Minus className="w-4 h-4" />
                            </button>
                            <span className="w-8 text-center font-bold">{item.quantity}</span>
                            <button
                              onClick={() => updateCartQuantity(item.id, 1)}
                              className="w-8 h-8 bg-gray-600 hover:bg-gray-500 rounded-full flex items-center justify-center"
                            >
                              <Plus className="w-4 h-4" />
                            </button>
                          </div>
                          <button
                            onClick={() => removeFromCart(item.id)}
                            className="text-red-400 hover:text-red-300"
                          >
                            Remove
                          </button>
                        </div>
                      </div>
                    ))}
                  </div>

                  <div className="border-t border-gray-600 pt-4 flex items-center justify-between">
                    <div className="flex items-center space-x-2">
                      <Star className="w-5 h-5 text-yellow-400" />
                      <span className="text-xl font-bold text-yellow-400">
                        Total: {getTotalCartCost()}
                      </span>
                    </div>
                    <button
                      onClick={checkout}
                      disabled={getTotalCartCost() > points}
                      className="bg-orange-500 hover:bg-orange-600 disabled:bg-gray-600 text-white px-6 py-2 rounded-lg font-bold transition-all"
                    >
                      {getTotalCartCost() > points ? 'Insufficient Points' : 'Checkout'}
                    </button>
                  </div>
                </div>
              )}
            </div>
          </div>
        )}
      </div>
    </div>
  );
};

export default App;