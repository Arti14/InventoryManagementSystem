var rootURL = "/app/api/inventory";
getProductDetailById = function() {
	var productId = $("#productDetailInput").val();
	productId = parseInt(productId);
	if(typeof(productId) != 'undefined' && (!isNaN(productId)) ) {
		$.ajax({
	        type: 'GET',
	        url: rootURL + '/' + productId,
	        datatype: 'json',
	        success: function(data){
	        	$("#productDetail").html(JSON.stringify(data));
	            $("#productDetail").show();
	        },
	        error: function (xhr, ajaxOptions, thrownError) {
		        $("#productDetail").html("Product Id does not exist!");
	            $("#productDetail").show();
		    }
    	});
	} else {
		$("#productDetail").html("Value is not a number, enter a valid product Id!");
	    $("#productDetail").show();
	}
}

addNewProductInInventory = function() {
	$.fn.serializeObject = function() {
	    var o = {};
	    var a = this.serializeArray();
	    $.each(a, function() {
	        if (o[this.name] !== undefined) {
	            if (!o[this.name].push) {
	                o[this.name] = [o[this.name]];
	            }
	            o[this.name].push(this.value || '');
	        } else {
	            o[this.name] = this.value || '';
	        }
	    });
	    return o;
	};

	var productData = $('#addForm').serializeObject();
	//alert(JSON.stringify(productData));
	$.ajax({
	        type: 'POST',
	        url: rootURL + '/add',
	        contentType: 'application/json',
	        datatype: 'json',
        	data: JSON.stringify(productData),
	        success: function(data, textStatus, jqXHR){
	            alert('product created successfully');
	            
	        },
	        error: function(jqXHR, textStatus, errorThrown){
	            console.log(errorThrown);
	            console.log(textStatus);
	            console.log(jqXHR);
	        }
	  });
}

checkoutInventory = function() {
	var productId = $("#checkout-productId").val();
	var quantity = $("#checkout-quantity").val();
	var innerjson = {};
	innerjson[productId] = parseInt(quantity);
	var jsonData = {};
	jsonData["map"] = innerjson;
	//console.log(JSON.stringify(jsonData));
	$.ajax({
        type: 'POST',
        url: rootURL + '/checkout',
        contentType: 'application/json',
        datatype: 'json',
    	data: JSON.stringify(jsonData),
        success: function(data, textStatus, jqXHR){
            alert('checkout successfull');
            
        },
        error: function(jqXHR, textStatus, errorThrown){
            console.log(errorThrown);
            console.log(textStatus);
            console.log(jqXHR);
        }
  });
	
}
getTransactionHistory = function() {
	var hours = $("#hours").val();
	hours = parseInt(hours);
	if(typeof(hours) != 'undefined' && !isNaN(hours) ) {
		$.ajax({
	        type: 'GET',
	        url: rootURL + '/sales_history/' + hours,
	        datatype: 'json',
	        success: function(data){
	        	data = flattenJsonData(data);
	        	createTransactionHistoryGrid(data);
	        },
	        error: function (xhr, ajaxOptions, thrownError) {
		        $("#transactionHistoryPanel").html("Error occured");
	            $("#transactionHistoryPanel").show();
		    }
    	});
	} else {
		$("#transactionHistoryPanel").html("Please enter a numerical value for hours");
	    $("#transactionHistoryPanel").show();
	}
}

flattenJsonData = function(data) {
	console.log(JSON.stringify(data));
	var flattenData = [];
	for(key in data) {
		var row = {};
		row["transId"] = data[key]["id"]["transId"];
		row["productId"] = data[key]["id"]["productId"];
		row["quantity"] = data[key]["quantity"];
		row["createdAt"] = data[key]["createdAt"];
		row["transType"] = data[key]["transType"];
		flattenData.push(row);
		//console.log("row "+JSON.stringify(row));
	}
	//console.log("flattenData "+JSON.stringify(flattenData));
	return flattenData;
}

/*$('#checkout-form').submit(function(){        //the user has clicked on submit

    //do your error checking and form validation here

    if (!errors)
    {
        $('#checkout-form').ajaxSubmit(function(data){        //submit the form using the form plugin
            console.log(JSON.stringify(data));    //here data will be the filename returned by the first PHP script
        });
    }
});*/
createTransactionHistoryGrid = function(dataToLoad) {
	//$('#list1').jqGrid('GridDestroy');
	jQuery('#list1').jqGrid('clearGridData');
	jQuery('#list1').jqGrid('setGridParam', {data: dataToLoad});
	jQuery('#list1').trigger('reloadGrid');
	jQuery("#list1").jqGrid({
	   	data: dataToLoad,
		datatype: "local",
	   	colNames:['Transaction Id','Product Id','Quantity','Transaction timestamp', 'Transaction type'],
	   	colModel:[
	   	    {name:'transId',  index:'transId', width:100 },
	   	    {name:'productId', width:100, },
	   		{name:'quantity', index:'transId', width:45},
	   		{name:'createdAt', index:'createdAt', width:110},
	   		{name:'transType', index:'transType', width:50}	
	   	],
	   	rowNum:5,
	   	rowList:[5, 7, 10],
	   	pager: '#pager1',
	   	sortname: 'transId',
	    viewrecords: true,
	    sortorder: "desc",
	    caption:"Transaction History",
        width:1000,
        gridview: true,
        viewrecords: true,
        rownumbers: true
	});
	jQuery("#list").jqGrid('navGrid','#pager1',{edit:false,add:false,del:false});
}