export API_KEY="sk_aDjk0JmtHCg9FZgiURtgKSKy9cxz3WD9OVflhg43-qQ"

curl "https://api.ppinfra.com/v3/openai/chat/completions" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer ${API_KEY}" \
  -d '{
    "model": "deepseek/deepseek-v3-0324",
    "messages": [
        {
            "role": "system",
            "content": "1+1"
        },
       {
            "role": "user",
            "content": "1+1"
        }
    ],
    "max_tokens": 512
}'