<?php
abstract class Notify{
	private $targetId;
	private $msgHandle;
	private $msg;
	public function __consctruct($targetId,$msg){
		$this->targetId=$targetId;
		$this->msg=$msg;
	}
	public function setMsghandle($handle){
		$this->msgHandle=$handle;
	}
	abstract public function send();
	
	abstract public function getPassportId();
}
class AffNotify extends Notify{


	public function send(){
		$this->sendToAff();
		$this->sendToAm();
	}
	private function sendToAff(){
		$this->msgHandle->send($this->targetId,$this->msg);
	}
	private function sendToAm(){
		$amId=$this->getAmIdByAffId($this->targetId);
		$this->msgHandle->send($amId,$this->msg);
	}
	
}
class OfferNotify extends Notify{

	public function send(){
		$this->sendToBd();
	}
	private function sendToBd(){
		$bdId=$this->getBdIdByOfferId($this->targetId);
		$this->msgHandle->send($bdId,$this->msg);
	}
}

abstract class MessageHandle{

	public function __construct(){
	}
	abstract public function send($passport_id,$msg);
}
class EmailHandle extends MessageHandle{
	public function send($passport_id,$msg){
		$email=$this->getEmailById($passport_id);
		$this->send($email,$msg);
	}
}
class InternalMsgHandle extends MessageHandle{

	public function send($passport_id,$msg){
		$this->insert($passport_id,$msg);
	}
}
class EmailAndMsgHandle extends MessageHandle{
	public function send($passport_id,$msg){
		$emailHandle=new EmailHandle();
		$emailHandle->send($passport_id,$msg);
		$msghandle=new InternalMsgHandle();
		$msgHandle->send($passport_id,$msg);
	}
}
if($is_offer_notify){
	$notify=new OfferNotify($offerId,$msg);
	if($is_send_email){
		$msgHandle=new EmailHandle()
	}
	if($is_send_msg){
		$msgHandle=new InternalMsgHandle();
	}

	$notify->setMsghandle($msgHandle);
	$notify->send();
}
