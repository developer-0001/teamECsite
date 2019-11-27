function checkValue(){
	var checkList = document.getElementsByClassName("checkList");
	var checkFlag = 0;
	for(var i = 0; i<checkList.length; i++){

		if(checkList[i].checked){
			checkFlag = 1;
			break;
		}
	}
	if(checkFlag == 1){
		document.getElementById('deleteButton').disabled="";
	}else{
		document.getElementById('deleteButton').disabled="true";
	}
}
function goSettlementConfirmAction(){
	document.getElementById("cartForm").action="SettlementConfirmAction";
}
function goGoLoginActionForCartForm(){
	document.getElementById("cartForm").action="GoLoginAction";
}