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
	public class RemoteSpeakerStreamStats{
#if __IOS__
		const string importLib = "__Internal";
#else
		const string importLib = "libVidyoClient";
#endif
		private IntPtr objPtr; // opaque VidyoRemoteSpeakerStreamStats reference.
		public IntPtr GetObjectPtr(){
			return objPtr;
		}
		[DllImport(importLib, CallingConvention = CallingConvention.Cdecl)]
		private static extern uint VidyoRemoteSpeakerStreamStatsGetbitsPerSampleNative(IntPtr obj);

		[DllImport(importLib, CallingConvention = CallingConvention.Cdecl)]
		private static extern uint VidyoRemoteSpeakerStreamStatsGetcodecDtxNative(IntPtr obj);

		[DllImport(importLib, CallingConvention = CallingConvention.Cdecl)]
		private static extern IntPtr VidyoRemoteSpeakerStreamStatsGetcodecNameNative(IntPtr obj);

		[DllImport(importLib, CallingConvention = CallingConvention.Cdecl)]
		private static extern uint VidyoRemoteSpeakerStreamStatsGetcodecQualitySettingNative(IntPtr obj);

		[DllImport(importLib, CallingConvention = CallingConvention.Cdecl)]
		private static extern IntPtr VidyoRemoteSpeakerStreamStatsGetnameNative(IntPtr obj);

		[DllImport(importLib, CallingConvention = CallingConvention.Cdecl)]
		private static extern uint VidyoRemoteSpeakerStreamStatsGetnumberOfChannelsNative(IntPtr obj);

		[DllImport(importLib, CallingConvention = CallingConvention.Cdecl)]
		private static extern uint VidyoRemoteSpeakerStreamStatsGetsampleRateNative(IntPtr obj);

		[DllImport(importLib, CallingConvention = CallingConvention.Cdecl)]
		private static extern uint VidyoRemoteSpeakerStreamStatsGetsendNetworkBitRateNative(IntPtr obj);

		[DllImport(importLib, CallingConvention = CallingConvention.Cdecl)]
		private static extern ulong VidyoRemoteSpeakerStreamStatsGetsendNetworkRttNative(IntPtr obj);

		public uint bitsPerSample;
		public uint codecDtx;
		public String codecName;
		public uint codecQualitySetting;
		public String name;
		public uint numberOfChannels;
		public uint sampleRate;
		public uint sendNetworkBitRate;
		public ulong sendNetworkRtt;
		public RemoteSpeakerStreamStats(IntPtr obj){
			objPtr = obj;

			bitsPerSample = VidyoRemoteSpeakerStreamStatsGetbitsPerSampleNative(objPtr);
			codecDtx = VidyoRemoteSpeakerStreamStatsGetcodecDtxNative(objPtr);
			codecName = (string)MarshalPtrToUtf8.GetInstance().MarshalNativeToManaged(VidyoRemoteSpeakerStreamStatsGetcodecNameNative(objPtr));
			codecQualitySetting = VidyoRemoteSpeakerStreamStatsGetcodecQualitySettingNative(objPtr);
			name = (string)MarshalPtrToUtf8.GetInstance().MarshalNativeToManaged(VidyoRemoteSpeakerStreamStatsGetnameNative(objPtr));
			numberOfChannels = VidyoRemoteSpeakerStreamStatsGetnumberOfChannelsNative(objPtr);
			sampleRate = VidyoRemoteSpeakerStreamStatsGetsampleRateNative(objPtr);
			sendNetworkBitRate = VidyoRemoteSpeakerStreamStatsGetsendNetworkBitRateNative(objPtr);
			sendNetworkRtt = VidyoRemoteSpeakerStreamStatsGetsendNetworkRttNative(objPtr);
		}
	};
}
