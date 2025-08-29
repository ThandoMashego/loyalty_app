// // src/services/customerService.js
// const DEFAULT_BASE_URL =
//     (import.meta && import.meta.env && import.meta.env.VITE_API_BASE_URL) ||
//     (typeof process !== "undefined" && process.env && process.env.REACT_APP_API_BASE_URL) ||
//     "http://localhost:4000";
//
// async function request(path, { method = "GET", body, signal } = {}) {
//     const res = await fetch(`${DEFAULT_BASE_URL}${path}`, {
//         method,
//         headers: { "Content-Type": "application/json", Accept: "application/json" },
//         body: body ? JSON.stringify(body) : undefined,
//         signal,
//     });
//
//     if (!res.ok) {
//         let message = res.statusText;
//         try {
//             const data = await res.json();
//             message = data?.message || message;
//         } catch (_) {}
//         throw new Error(`${message} (HTTP ${res.status})`);
//     }
//     // Try JSON, fall back to text
//     const text = await res.text();
//     try { return text ? JSON.parse(text) : null; } catch { return text; }
// }
//
// const customerService = {
//     registerCustomer(id, payload, signal) {
//         return request(`/customers/${id}`, { method: "POST", body: payload, signal });
//     },
//
//     sendMoney(id, payload, signal) {
//         return request(`/customers/${id}/send`, { method: "POST", body: payload, signal });
//     },
//
//     async getPoints(id, signal) {
//         const data = await request(`/customers/${id}/points`, { signal });
//         if (typeof data === "number") return { points: data };
//         if (data && typeof data.points === "number") return data;
//         return { points: Number(data?.points ?? 0) };
//     },
//
//     getTransactions(id, signal) {
//         return request(`/customers/${id}/transactions`, { signal });
//     },
//
//     getRewards(signal) {
//         return request(`/rewards`, { signal });
//     },
//
//     redeemReward(id, rewardId, payload = {}, signal) {
//         return request(`/customers/${id}/redeem/${rewardId}`, {
//             method: "POST",
//             body: payload,
//             signal,
//         });
//     },
// };
//
// export default customerService;


// services/customer.service.js

const API_BASE_URL = 'http://localhost:4000'; // Your Java server URL

class CustomerService {

    // Helper method to handle API responses
    async handleResponse(response) {
        if (!response.ok) {
            const errorText = await response.text();
            throw new Error(`HTTP ${response.status}: ${errorText}`);
        }

        // Check if response has content
        const contentType = response.headers.get('content-type');
        if (contentType && contentType.includes('application/json')) {
            return await response.json();
        }

        return await response.text();
    }

    // Register a new user
    async registerUser(phone, name = "Customer") {
        try {
            const response = await fetch(`${API_BASE_URL}/users`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify({
                    phone: phone,
                    name: name
                })
            });

            return await this.handleResponse(response);
        } catch (error) {
            console.error('Error registering user:', error);
            throw error;
        }
    }

    // Send money (creates transaction and earns points)
    async sendMoney(userId, amount) {
        try {
            const response = await fetch(`${API_BASE_URL}/users/${userId}/send`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify({
                    amount: amount
                })
            });

            return await this.handleResponse(response);
        } catch (error) {
            console.error('Error sending money:', error);
            throw error;
        }
    }

    // Get user points
    async getPoints(userId) {
        try {
            const response = await fetch(`${API_BASE_URL}/users/${userId}/points`, {
                method: 'GET',
                headers: {
                    'Content-Type': 'application/json',
                }
            });

            return await this.handleResponse(response);
        } catch (error) {
            console.error('Error getting points:', error);
            throw error;
        }
    }

    // Get user transactions
    async getTransactions(userId) {
        try {
            const response = await fetch(`${API_BASE_URL}/users/${userId}/transactions`, {
                method: 'GET',
                headers: {
                    'Content-Type': 'application/json',
                }
            });

            return await this.handleResponse(response);
        } catch (error) {
            console.error('Error getting transactions:', error);
            throw error;
        }
    }

    // Get all rewards
    async getRewards() {
        try {
            const response = await fetch(`${API_BASE_URL}/rewards`, {
                method: 'GET',
                headers: {
                    'Content-Type': 'application/json',
                }
            });

            return await this.handleResponse(response);
        } catch (error) {
            console.error('Error getting rewards:', error);
            throw error;
        }
    }

    // Add a new reward (admin function)
    async addReward(name, cost, description, icon, stock) {
        try {
            const response = await fetch(`${API_BASE_URL}/rewards`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify({
                    name: name,
                    cost: cost,
                    description: description,
                    icon: icon,
                    stock: stock
                })
            });

            return await this.handleResponse(response);
        } catch (error) {
            console.error('Error adding reward:', error);
            throw error;
        }
    }

    // Redeem a reward
    async redeemReward(userId, rewardId) {
        try {
            const response = await fetch(`${API_BASE_URL}/users/${userId}/rewards/${rewardId}/redeem`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                }
            });

            return await this.handleResponse(response);
        } catch (error) {
            console.error('Error redeeming reward:', error);
            throw error;
        }
    }
}

// Create and export a singleton instance
const customerService = new CustomerService();
export default customerService;