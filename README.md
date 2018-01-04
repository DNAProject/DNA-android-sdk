# DNA : JavaScript-SDK

> Onchain DNA项目的JS-SDK。
  
## Demo:
DNAWalletSDK.apk



## 功能列表：
- 生成钱包
  - 新建钱包
  - 从私钥导入
- 发送交易
  - 选择钱包
  - 打开钱包
    - 通过密钥文件
    - 通过私钥
  - 钱包信息
    - 账户信息
      - 地址
      - 当前区块节点高度
      - 账户余额
  - 发送交易
    - 交易类型
      - Transfer Asset
        - 发送到对方地址
        - 发送数量
        - 发送自我的地址
- 切换中英文
- 切换连接节点



## API列表：

- #### Create account - 新建账户
```angular2html
/**
 * 生成随机私钥
 * 
 * @return Account account
 */
public static Account createAccount() {
    return account;
}


```
```angular2html
/**
 * Create account use random private key.
 * 新建一个账户。
 *
 * @param byte[] privateKey
 *
 * @return Account account
 */
public static Account createAccount(byte[] privateKey) {
    return account;
}
```

- #### Make transfer transaction - 转账交易
```angular2html
/**
 * Make transfer transaction and get transaction unsigned data.
 * 发起一个转账交易和获取交易数据（十六进制）。
 * 
 * @param AssetInfo Asset
 * @param byte[] publicKeyEncoded
 * @param String toAddress
 * @param Double transferAssetAmount
 * 
 * @returns String data
 */
public static String makeTransferTransaction(AssetInfo Asset, byte[] publicKeyEncoded, String toAddress, Double transferAssetAmount){
    return data;
}

```

- #### Signature transaction - 生成签名
```angular2html
/**
 * Signature transaction unsigned Data.
 * 生成签名。
 * 
 * @param String txData
 * @param byte[] privateKey
 * 
 * @return byte[] signature
 */
public static byte[] signatureData(String txData, byte[] privateKey) {
    return signature;
}
```

- #### Program hash to address - 脚本哈希转地址
```angular2html
/**
 * Program hash to address.
 * 脚本哈希转地址。
 * 
 * @param byte[] programHash
 * 
 * @return String *
 */
public String getAddress(byte[] programHash) {	
    return Base58.encode(datas);
}
```

##### Get information about user accounts, transactions-获取用户账户、交易等信息
```angular2html
/**
 * Get information about user accounts, transactions.
 * 获取用户账户、交易等信息
 * @param String nodeAPI
 * @param Account account
 * @constructor
 */
public static void getUpspent(final String nodeAPI ,final Account account) {}；
```

##### Refresh the height of node-获取连接节点的区块高度
```angular2html
/**
 * Refresh the height of node
 * 获取节点高度
 * @param String nodeAPI
 * @constructor
 */
public static String getNodeHeight(final String nodeAPI) {};
```

##### Initiate a transaction-发起交易
```angular2html
/**
 * Initiate a transaction
 * 发起交易
 * @param String txRawData
 * @constructor
 */
public static void SendTransactionData(final String txRawData) {};
```