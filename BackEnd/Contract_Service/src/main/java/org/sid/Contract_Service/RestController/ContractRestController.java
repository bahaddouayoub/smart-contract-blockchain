package org.sid.Contract_Service.RestController;

import io.reactivex.Flowable;
import org.sid.Contract_Service.entities.House;
import org.springframework.web.bind.annotation.*;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.response.Transaction;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.RawTransactionManager;
import org.web3j.tx.TransactionManager;
import org.web3j.tx.Transfer;
import org.web3j.utils.Convert;

import java.math.BigDecimal;
import java.math.BigInteger;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("Ethereum")
public class ContractRestController {

    //********************    ADMIN    *****************************//

    private  final static  String PRIVATE_KEY="0xb5d0ac81bc1b357340b4c0e90f779da9511a636d6f68baa27a1819293c1b200a";
    private final static BigInteger GAS_LIMIT =BigInteger.valueOf(6721975);
    private final static BigInteger GAS_PRICE = BigInteger.valueOf(20000000000L);
    private  static String  CONTRACT_ADDRESS = "0xD9fAf8b8bdd67eA65b68E6DF356B07C8F744942d";

    Web3j web3j = Web3j.build( new HttpService("HTTP://ganache:8545"));
    TransactionManager transactionManager = new RawTransactionManager(

            web3j,getCredentialsFromPrivateKey() );

    @GetMapping(path = "/hello")
    public String hello() throws Exception {

        return "hello blockchain ";
    }

    @PostMapping("/DeployContract")
    public void  DeployContract() throws Exception {

        String deployedAddress = deployContract(web3j,transactionManager);
        CONTRACT_ADDRESS=deployedAddress;
        System.out.println("Deployed contract address :"+ CONTRACT_ADDRESS);
    }

    //PostMapping
    @PostMapping(path = "/AddHouse")
    public void AddHouse(@RequestParam(value = "ownerAddress") String ownerAddress,
                         @RequestParam(value = "title") String  title) throws Exception {

        System.out.println("*****************"+ownerAddress);
        System.out.println("*****************"+title);
        _contract loadAddress = loadContract(CONTRACT_ADDRESS,web3j,transactionManager);
        loadAddress.addHouse(ownerAddress,title).send();


    }

    @PostMapping(value = "/transferHouse")
    public void transferHouse(@RequestParam(value = "receiver") String receiver,
                              @RequestParam(value = "amount") Long amount,
                              @RequestParam(value = "title") String title) throws Exception {



        //transfer House
        System.out.println("title=====> "+title);
        _contract loadAddress = loadContract(CONTRACT_ADDRESS,web3j,transactionManager);
        System.out.println("Deployed contract address :"+ loadAddress.getContractAddress());
        loadAddress.transferLand("0xF1fACbfbF6c845F90A0E44eC6cf4eD12759b0464",title).send();

        //transfer ETHER
       // transferEther(receiver,amount);
    }

    @GetMapping("/transactions")
    public Flowable<Transaction> getTransactions() throws Exception {
        Flowable<org.web3j.protocol.core.methods.response.Transaction> transactions = web3j.replayPastTransactionsFlowable(DefaultBlockParameterName.EARLIEST,
                DefaultBlockParameterName.LATEST).serialize();
        return transactions;
    }


    private Credentials getCredentialsFromPrivateKey(){
        return  Credentials.create(PRIVATE_KEY);
    }

    //*********************** deploy the smart contract en ethereum   *********************//

    private String deployContract(Web3j web3j,TransactionManager transactionManager) throws Exception {

        return MyHouseContract.deploy(web3j,transactionManager,GAS_PRICE,GAS_LIMIT).send().getContractAddress();

    }

    //*********************** load the smart contract en ethereum   *********************//

    private _contract loadContract(String contractAddress, Web3j web3j, TransactionManager transactionManager){
        //CONTRACT_ADDRESS
        return _contract.load(contractAddress,web3j,transactionManager,GAS_PRICE,GAS_LIMIT);


    }



    //**************************************  END ADMIN********************************//



    //**************************************  Client ********************************//

    @GetMapping(value = "/transferEther")
    public void transferEther(@RequestParam(value = "receiver") String receiver ,
                              @RequestParam(value = "amount") Long amount ) throws Exception {

        _contract loadAddress = loadContract(CONTRACT_ADDRESS,web3j,transactionManager);
        System.out.println("Deployed contract address :"+ loadAddress.getContractAddress());
        //transfer on Ganache
        Transfer transfer =new Transfer(web3j,transactionManager);
        TransactionReceipt transactionReceipt = transfer.sendFunds(
                receiver, // amount receptor
                //BigDecimal.ONE,// amount to transfer
                BigDecimal.valueOf(amount),
                Convert.Unit.ETHER, // one ether
                GAS_PRICE,
                GAS_LIMIT
        ).send();

        System.out.println("transaction hash:"+transactionReceipt.getTransactionHash() );

    }

    @GetMapping(value = "/getNombreOfHouses")
    public BigInteger getNombreOfHouses(@RequestParam(value = "ownerAddress") String ownerAddress ) throws Exception {

        return BigInteger.valueOf(0);

    }

    @GetMapping(value = "/getHouse")
    public House getHouse(@RequestParam(value = "_HouseHolder") String _HouseHolder,
                          @RequestParam(value = "title") String title) throws Exception {

        House house = new House();

            // body

        //return loadAddress.getHouse(_HouseHolder,BigInteger.valueOf(_index)).send();
        return  house;
    }

}

