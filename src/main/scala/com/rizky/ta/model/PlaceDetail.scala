package com.rizky.ta.model

import com.rizky.ta.model.Common.{AddressComponents, Geometry}

/**
  * Created by risol_000 on 1/30/2017.
  */




case class PlaceDetailResult(address_components: AddressComponents, adr_address: String, formatted_address: String,
                             formatted_phone_number: String, geometry: Geometry, icon: String, id: String,
                             international_phone_number: String, name: String, place_id: String, reference: String,
                             scope: String, types: List[String], url: String, utc_offset: Integer, vicinity: String)

case class PlaceDetailModel(html_attributions: List[String], result: PlaceDetailResult, status: String)

class PlaceDetail {

}
