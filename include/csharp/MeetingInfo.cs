// DO NOT EDIT! This is an autogenerated file. All changes will be
// overwritten!

//	Copyright (c) 2016 Vidyo, Inc. All rights reserved.


using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Collections;
using System.Runtime.InteropServices;

namespace VidyoClient
{
	public class MeetingInfo{
#if __IOS__
		const string importLib = "__Internal";
#else
		const string importLib = "libVidyoClient";
#endif
		private IntPtr objPtr; // opaque VidyoMeetingInfo reference.
		public IntPtr GetObjectPtr(){
			return objPtr;
		}
		[DllImport(importLib, CallingConvention = CallingConvention.Cdecl)]
		private static extern IntPtr VidyoMeetingInfoGetattendeesNative(IntPtr obj);

		[DllImport(importLib, CallingConvention = CallingConvention.Cdecl)]
		private static extern IntPtr VidyoMeetingInfoGetattendeesArrayNative(IntPtr obj, ref int size);

		[DllImport(importLib, CallingConvention = CallingConvention.Cdecl)]
		private static extern void VidyoMeetingInfoFreeattendeesArrayNative(IntPtr obj, int size);

		[DllImport(importLib, CallingConvention = CallingConvention.Cdecl)]
		private static extern IntPtr VidyoMeetingInfoGetbodyNative(IntPtr obj);

		[DllImport(importLib, CallingConvention = CallingConvention.Cdecl)]
		private static extern IntPtr VidyoMeetingInfoGetendDateTimeNative(IntPtr obj);

		[DllImport(importLib, CallingConvention = CallingConvention.Cdecl)]
		private static extern IntPtr VidyoMeetingInfoGetidNative(IntPtr obj);

		[DllImport(importLib, CallingConvention = CallingConvention.Cdecl)]
		private static extern Boolean VidyoMeetingInfoGetisVidyoMeetingNative(IntPtr obj);

		[DllImport(importLib, CallingConvention = CallingConvention.Cdecl)]
		private static extern IntPtr VidyoMeetingInfoGetlocationNative(IntPtr obj);

		[DllImport(importLib, CallingConvention = CallingConvention.Cdecl)]
		private static extern uint VidyoMeetingInfoGetnumOfAttendeesNative(IntPtr obj);

		[DllImport(importLib, CallingConvention = CallingConvention.Cdecl)]
		private static extern IntPtr VidyoMeetingInfoGetownerEmailNative(IntPtr obj);

		[DllImport(importLib, CallingConvention = CallingConvention.Cdecl)]
		private static extern IntPtr VidyoMeetingInfoGetownerNameNative(IntPtr obj);

		[DllImport(importLib, CallingConvention = CallingConvention.Cdecl)]
		private static extern IntPtr VidyoMeetingInfoGetstartDateTimeNative(IntPtr obj);

		[DllImport(importLib, CallingConvention = CallingConvention.Cdecl)]
		private static extern IntPtr VidyoMeetingInfoGetsubjectNative(IntPtr obj);

		[DllImport(importLib, CallingConvention = CallingConvention.Cdecl)]
		private static extern uint VidyoMeetingInfoGettotalBodyLengthNative(IntPtr obj);

		[DllImport(importLib, CallingConvention = CallingConvention.Cdecl)]
		private static extern IntPtr VidyoMeetingInfoGetvidyoMeetingRoomLinkNative(IntPtr obj);

		public List<MeetingAttendee> attendees;
		public String body;
		public String endDateTime;
		public String id;
		public Boolean isVidyoMeeting;
		public String location;
		public uint numOfAttendees;
		public String ownerEmail;
		public String ownerName;
		public String startDateTime;
		public String subject;
		public uint totalBodyLength;
		public String vidyoMeetingRoomLink;
		public MeetingInfo(IntPtr obj){
			objPtr = obj;

			List<MeetingAttendee> csAttendees = new List<MeetingAttendee>();
			int nAttendeesSize = 0;
			IntPtr nAttendees = VidyoMeetingInfoGetattendeesArrayNative(VidyoMeetingInfoGetattendeesNative(objPtr), ref nAttendeesSize);
			int nAttendeesIndex = 0;
			while (nAttendeesIndex < nAttendeesSize) {
				MeetingAttendee csTattendees = new MeetingAttendee(Marshal.ReadIntPtr(nAttendees + (nAttendeesIndex * Marshal.SizeOf(nAttendees))));
				csAttendees.Add(csTattendees);
				nAttendeesIndex++;
			}

			attendees = csAttendees;
			body = (string)MarshalPtrToUtf8.GetInstance().MarshalNativeToManaged(VidyoMeetingInfoGetbodyNative(objPtr));
			endDateTime = (string)MarshalPtrToUtf8.GetInstance().MarshalNativeToManaged(VidyoMeetingInfoGetendDateTimeNative(objPtr));
			id = (string)MarshalPtrToUtf8.GetInstance().MarshalNativeToManaged(VidyoMeetingInfoGetidNative(objPtr));
			isVidyoMeeting = VidyoMeetingInfoGetisVidyoMeetingNative(objPtr);
			location = (string)MarshalPtrToUtf8.GetInstance().MarshalNativeToManaged(VidyoMeetingInfoGetlocationNative(objPtr));
			numOfAttendees = VidyoMeetingInfoGetnumOfAttendeesNative(objPtr);
			ownerEmail = (string)MarshalPtrToUtf8.GetInstance().MarshalNativeToManaged(VidyoMeetingInfoGetownerEmailNative(objPtr));
			ownerName = (string)MarshalPtrToUtf8.GetInstance().MarshalNativeToManaged(VidyoMeetingInfoGetownerNameNative(objPtr));
			startDateTime = (string)MarshalPtrToUtf8.GetInstance().MarshalNativeToManaged(VidyoMeetingInfoGetstartDateTimeNative(objPtr));
			subject = (string)MarshalPtrToUtf8.GetInstance().MarshalNativeToManaged(VidyoMeetingInfoGetsubjectNative(objPtr));
			totalBodyLength = VidyoMeetingInfoGettotalBodyLengthNative(objPtr);
			vidyoMeetingRoomLink = (string)MarshalPtrToUtf8.GetInstance().MarshalNativeToManaged(VidyoMeetingInfoGetvidyoMeetingRoomLinkNative(objPtr));
			VidyoMeetingInfoFreeattendeesArrayNative(nAttendees, nAttendeesSize);
		}
	};
}
